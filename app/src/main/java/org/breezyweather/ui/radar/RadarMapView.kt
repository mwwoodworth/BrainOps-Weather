package org.breezyweather.ui.radar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import breezyweather.domain.location.model.Location
import org.breezyweather.BuildConfig
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay
import android.util.Log
import android.view.Choreographer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.net.HttpURLConnection
import java.net.URL

/**
 * High-performance radar map view optimized for 120Hz+ displays.
 * Uses RainViewer for international locations and NOAA NEXRAD for US locations.
 */
@Composable
fun RadarMapView(
    location: Location,
    layers: List<RadarLayer>,
    animationState: RadarAnimationState,
    onLayerToggle: (RadarLayer) -> Unit,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    onTimestampsLoaded: ((List<Long>) -> Unit)? = null
) {
    val context = LocalContext.current
    // Store all available radar timestamps for animation
    var radarTimestamps by remember { mutableStateOf<List<Long>>(emptyList()) }
    var currentFrameIndex by remember { mutableStateOf(-1) }
    var radarOverlay: TilesOverlay? by remember { mutableStateOf(null) }
    val isDark = isSystemInDarkTheme()

    // Initialize OSMDroid configuration with 120Hz optimizations
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        // Increase tile download threads for faster loading
        Configuration.getInstance().tileDownloadThreads = 6
        Configuration.getInstance().tileFileSystemThreads = 4
        // Larger cache for smoother animation
        Configuration.getInstance().tileFileSystemCacheMaxBytes = 100L * 1024 * 1024 // 100MB
        Configuration.getInstance().tileFileSystemCacheTrimBytes = 80L * 1024 * 1024 // 80MB trim

        // Fetch all available RainViewer timestamps for animation
        val timestamps = fetchAllRainViewerTimestamps()
        if (timestamps.isNotEmpty()) {
            radarTimestamps = timestamps
            currentFrameIndex = timestamps.size - 1 // Start at most recent
            onTimestampsLoaded?.invoke(timestamps)
        }
    }

    // Periodically refresh radar timestamps so new frames show up
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2 * 60 * 1000L) // 2 minutes - more frequent updates
            val timestamps = fetchAllRainViewerTimestamps()
            if (timestamps.isNotEmpty() && timestamps != radarTimestamps) {
                radarTimestamps = timestamps
                onTimestampsLoaded?.invoke(timestamps)
            }
        }
    }

    // Update frame based on animation progress
    LaunchedEffect(animationState.progress, radarTimestamps) {
        if (radarTimestamps.isNotEmpty()) {
            val newIndex = ((animationState.progress * (radarTimestamps.size - 1)).toInt())
                .coerceIn(0, radarTimestamps.size - 1)
            if (newIndex != currentFrameIndex) {
                currentFrameIndex = newIndex
            }
        }
    }

    // Create dark/light OSM tile sources (CartoDB Dark Matter for dark, Mapnik for light)
    val darkBasemap = remember {
        // Explicit server list to allow safe rotation
        val servers = listOf(
            "https://a.basemaps.cartocdn.com/dark_all/",
            "https://b.basemaps.cartocdn.com/dark_all/",
            "https://c.basemaps.cartocdn.com/dark_all/",
            "https://d.basemaps.cartocdn.com/dark_all/"
        )
        object : OnlineTileSourceBase(
            "CartoDB Dark Matter",
            0, 20, 256, ".png",
            servers.toTypedArray()
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                val server = servers[(x + y + zoom).mod(servers.size)]
                return "${server}${zoom}/${x}/${y}$mImageFilenameEnding"
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            // Default basemap will be set in theme effect below
            setMultiTouchControls(isInteractive)
            controller.setZoom(9.5)
            // Allow deep zooming (NOAA/DarkMatter go up to 18-20)
            maxZoomLevel = 19.0

            // Performance optimizations for 120fps+ on high-end devices
            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            isFlingEnabled = true
            setScrollableAreaLimitDouble(null) // No limit for smooth panning
            setUseDataConnection(true)
            setTilesScaledToDpi(true) // Crisper text on high DPI screens

            // Disable scroll if not interactive
            if (!isInteractive) {
                setOnTouchListener { _, _ -> true }
            }
        }
    }

    // Update location
    LaunchedEffect(location) {
        val point = GeoPoint(location.latitude, location.longitude)
        mapView.controller.setCenter(point)
    }

    // Update basemap when theme changes
    LaunchedEffect(isDark) {
        mapView.setTileSource(if (isDark) darkBasemap else TileSourceFactory.MAPNIK)
        mapView.invalidate()
    }

    // Update Layers - Smart multi-source radar with animation support
    LaunchedEffect(layers, location, currentFrameIndex, radarTimestamps) {
        // Remove previous radar overlay only (leave basemap intact)
        radarOverlay?.let { previous ->
            mapView.overlays.remove(previous)
            radarOverlay = null
        }

        // Add radar layer if precipitation is selected
        if (layers.any { it.layerId == "precipitation" }) {
            // Determine if location is in US (use NOAA) or international (use RainViewer)
            val isUSLocation = location.latitude >= 24.0 && location.latitude <= 50.0 &&
                              location.longitude >= -125.0 && location.longitude <= -66.0

            val tileSource = if (isUSLocation) {
                // NOAA NEXRAD Radar - Free forever, high quality, US only
                val noaaServers = arrayOf(
                    "https://mesonet.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/",
                    "https://mesonet1.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/"
                )
                object : OnlineTileSourceBase(
                    "NOAA NEXRAD Radar",
                    0, 18, 256, ".png",
                    noaaServers
                ) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val z = MapTileIndex.getZoom(pMapTileIndex)
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        return "${noaaServers[z.mod(noaaServers.size)]}$z/$x/$y.png"
                    }
                }
            } else {
                // RainViewer - Free globally, with proper frame animation
                val timestamp = if (currentFrameIndex >= 0 && currentFrameIndex < radarTimestamps.size) {
                    radarTimestamps[currentFrameIndex]
                } else if (radarTimestamps.isNotEmpty()) {
                    radarTimestamps.last()
                } else {
                    null
                }
                val tsSegment = timestamp?.toString() ?: "last"

                val rainViewerServers = arrayOf(
                    "https://tilecache.rainviewer.com/v2/radar/",
                    "https://a.tilecache.rainviewer.com/v2/radar/",
                    "https://b.tilecache.rainviewer.com/v2/radar/"
                )
                object : OnlineTileSourceBase(
                    "RainViewer Radar",
                    0, 12, 256, ".png", // RainViewer supports up to zoom 12
                    rainViewerServers
                ) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val z = MapTileIndex.getZoom(pMapTileIndex)
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        val server = rainViewerServers[(x + y).mod(rainViewerServers.size)]
                        // Color scheme: 2 = original, smooth: 1, snow: 1
                        return "${server}$tsSegment/256/$z/$x/$y/2/1_1.png"
                    }
                }
            }

            val tileProvider = MapTileProviderBasic(context)
            tileProvider.tileSource = tileSource

            val overlay = TilesOverlay(tileProvider, context)
            overlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
            overlay.loadingLineColor = android.graphics.Color.TRANSPARENT
            overlay.setLoadingDrawable(null) // No loading indicator for clean look
            mapView.overlays.add(overlay)
            radarOverlay = overlay
        }

        // Force redraw at 120Hz-friendly timing
        mapView.invalidate()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }
}

/**
 * Fetches all available RainViewer radar frame timestamps for animation.
 * RainViewer provides ~12 past frames (2 hours) in 10-minute intervals.
 */
private suspend fun fetchAllRainViewerTimestamps(): List<Long> = withContext(Dispatchers.IO) {
    val url = URL("https://tilecache.rainviewer.com/api/maps.json")
    return@withContext try {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
        }
        connection.inputStream.bufferedReader().use { reader ->
            val body = reader.readText()
            val jsonArray = JSONArray(body)
            val timestamps = mutableListOf<Long>()
            for (i in 0 until jsonArray.length()) {
                timestamps.add(jsonArray.getLong(i))
            }
            timestamps
        }
    } catch (e: Exception) {
        Log.e("RadarMapView", "Failed to fetch RainViewer timestamps", e)
        emptyList()
    }
}

/**
 * Fetches the most recent RainViewer radar frame timestamp (legacy helper).
 */
private suspend fun fetchLatestRainViewerTimestamp(): Long? = withContext(Dispatchers.IO) {
    val timestamps = fetchAllRainViewerTimestamps()
    timestamps.lastOrNull()
}
