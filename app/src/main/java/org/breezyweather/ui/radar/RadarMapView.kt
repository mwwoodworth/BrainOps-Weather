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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun RadarMapView(
    location: Location,
    layers: List<RadarLayer>,
    animationState: RadarAnimationState,
    onLayerToggle: (RadarLayer) -> Unit,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true
) {
    val context = LocalContext.current
    var rainViewerTimestamp by remember { mutableStateOf<Long?>(null) }

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        // Pre-fetch latest RainViewer timestamp to avoid 404/empty tiles
        rainViewerTimestamp = fetchLatestRainViewerTimestamp()
    }

    // Create dark OSM tile source - CartoDB Dark Matter
    val darkBasemap = remember {
        object : OnlineTileSourceBase(
            "CartoDB Dark Matter",
            0, 20, 256, ".png",
            arrayOf(
                "https://a.basemaps.cartocdn.com/dark_all/",
                "https://b.basemaps.cartocdn.com/dark_all/",
                "https://c.basemaps.cartocdn.com/dark_all/",
                "https://d.basemaps.cartocdn.com/dark_all/"
            )
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return "${baseUrl[0]}$zoom/$x/$y$mImageFilenameEnding"
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            // Use dark CartoDB basemap
            setTileSource(darkBasemap)
            setMultiTouchControls(isInteractive)
            controller.setZoom(10.0)

            // Performance optimizations for 120fps+ on high-end devices
            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            isFlingEnabled = true
            setScrollableAreaLimitDouble(null) // No limit for smooth panning
            setUseDataConnection(true)

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

    // Update Layers - Smart multi-source radar
    LaunchedEffect(layers, location) {
        // Preserve base map (index 0) and clear other overlays (weather layers)
        if (mapView.overlays.isNotEmpty()) {
            val baseMap = mapView.overlays[0]
            mapView.overlays.clear()
            mapView.overlays.add(baseMap)
        }

        // Add radar layer if precipitation is selected
        if (layers.any { it.layerId == "precipitation" }) {
            // Determine if location is in US (use NOAA) or international (use RainViewer)
            val isUSLocation = location.latitude >= 24.0 && location.latitude <= 50.0 &&
                              location.longitude >= -125.0 && location.longitude <= -66.0

            val tileSource = if (isUSLocation) {
                // NOAA NEXRAD Radar - Free forever, high quality, US only
                object : OnlineTileSourceBase(
                    "NOAA NEXRAD Radar",
                    0, 18, 256, ".png",
                    arrayOf(
                        "https://mesonet.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/",
                        "https://mesonet.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/"
                    )
                ) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val z = MapTileIndex.getZoom(pMapTileIndex)
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        return "${baseUrl[0]}$z/$x/$y.png"
                    }
                }
            } else {
                // RainViewer - Free globally, no API key
                val timestamp = rainViewerTimestamp
                if (timestamp == null) {
                    Log.w("RadarMapView", "RainViewer timestamp unavailable; skipping radar overlay.")
                    null
                } else
                object : OnlineTileSourceBase(
                    "RainViewer Radar",
                    0, 12, 256, ".png",
                    arrayOf(
                        "https://tilecache.rainviewer.com/v2/radar/",
                        "https://tilecache.rainviewer.com/v2/radar/"
                    )
                ) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val z = MapTileIndex.getZoom(pMapTileIndex)
                        val x = MapTileIndex.getX(pMapTileIndex)
                        val y = MapTileIndex.getY(pMapTileIndex)
                        // RainViewer requires a valid timestamp; we use the freshest frame.
                        return "${baseUrl[0]}$timestamp/256/$z/$x/$y/2/1_1.png"
                    }
                }
            }

            if (tileSource != null) {
                val tileProvider = MapTileProviderBasic(context)
                tileProvider.tileSource = tileSource

                val overlay = TilesOverlay(tileProvider, context)
                overlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
                overlay.loadingLineColor = android.graphics.Color.TRANSPARENT
                overlay.setLoadingDrawable(null) // No loading indicator
                mapView.overlays.add(overlay)
            }
        }

        // Force redraw
        mapView.invalidate()
        mapView.postInvalidate()
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
 * Fetches the most recent RainViewer radar frame timestamp so tile requests resolve (avoids 404s).
 */
private suspend fun fetchLatestRainViewerTimestamp(): Long? = withContext(Dispatchers.IO) {
    val url = URL("https://tilecache.rainviewer.com/api/maps.json")
    return@withContext try {
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
        }
        connection.inputStream.bufferedReader().use { reader ->
            val body = reader.readText()
            val timestamps = JSONArray(body)
            // Use the newest frame (last in array)
            timestamps.optLong(timestamps.length() - 1)
        }
    } catch (e: Exception) {
        Log.e("RadarMapView", "Failed to fetch RainViewer timestamps", e)
        null
    }
}
