package org.breezyweather.ui.radar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import breezyweather.domain.location.model.Location
import org.breezyweather.BuildConfig
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

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

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    val mapView = remember {
        MapView(context).apply {
            // Use built-in MAPNIK source first to ensure OSMDroid works
            // We'll apply dark theming through ColorMatrix filter
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(isInteractive)
            controller.setZoom(10.0)

            // Apply dark theme filter to basemap tiles
            val darkPaint = android.graphics.Paint().apply {
                colorFilter = android.graphics.ColorMatrixColorFilter(
                    android.graphics.ColorMatrix().apply {
                        // Invert colors and reduce brightness for dark theme
                        setSaturation(0.3f) // Desaturate
                        val darkMatrix = floatArrayOf(
                            0.2f, 0f, 0f, 0f, -30f,  // Red
                            0f, 0.2f, 0f, 0f, -30f,  // Green
                            0f, 0f, 0.2f, 0f, -30f,  // Blue
                            0f, 0f, 0f, 1f, 0f       // Alpha
                        )
                        postConcat(android.graphics.ColorMatrix(darkMatrix))
                    }
                )
            }
            overlayManager.tilesOverlay.paint = darkPaint

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

    // Update Layers
    LaunchedEffect(layers) {
        // Clear existing weather overlays (keep base map)
        mapView.overlays.removeIf { it is TilesOverlay }

        layers.forEach { layer ->
            val tileSource = object : OnlineTileSourceBase(
                layer.title,
                0, 18, 256, ".png",
                arrayOf("https://tile.openweathermap.org/map/${layer.endpoint}/")
            ) {
                override fun getTileURLString(pMapTileIndex: Long): String {
                    val z = MapTileIndex.getZoom(pMapTileIndex)
                    val x = MapTileIndex.getX(pMapTileIndex)
                    val y = MapTileIndex.getY(pMapTileIndex)
                    return "${baseUrl[0]}$z/$x/$y.png?appid=${BuildConfig.OPEN_WEATHER_KEY}"
                }
            }

            val tileProvider = MapTileProviderBasic(context)
            tileProvider.tileSource = tileSource

            val overlay = TilesOverlay(tileProvider, context)
            overlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
            overlay.loadingLineColor = android.graphics.Color.TRANSPARENT
            mapView.overlays.add(overlay)
        }
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
