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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    // Create dark themed basemap tile source
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
                val z = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return "${baseUrl[0]}$z/$x/$y.png"
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(darkBasemap) // Dark themed basemap for better contrast
            setMultiTouchControls(true)
            controller.setZoom(10.0)
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
                    return "$baseUrl$z/$x/$y.png?appid=${BuildConfig.OPEN_WEATHER_KEY}"
                }
            }

            val overlay = TilesOverlay(MapTileProviderBasic(context, tileSource), context)
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

// Helper to avoid creating provider in composable loop
class MapTileProviderBasic(context: android.content.Context, tileSource: org.osmdroid.tileprovider.tilesource.ITileSource) : 
    org.osmdroid.tileprovider.MapTileProviderBasic(context, tileSource)
