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

    // Update Layers
    LaunchedEffect(layers) {
        // Preserve base map (index 0) and clear other overlays (weather layers)
        if (mapView.overlays.isNotEmpty()) {
            val baseMap = mapView.overlays[0]
            mapView.overlays.clear()
            mapView.overlays.add(baseMap)
        }

        // Add weather layers
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
            overlay.setLoadingDrawable(null) // No loading indicator
            mapView.overlays.add(overlay)
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
