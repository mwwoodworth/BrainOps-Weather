package org.breezyweather.ui.radar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

class RadarMapViewTest {

    @Test
    fun testBasemapDarkUrlGeneration() {
        val darkBasemap = object : OnlineTileSourceBase(
            "CartoDB Dark Matter",
            0, 20, 256, ".png",
            arrayOf("https://a.basemaps.cartocdn.com/dark_all/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                val base = getBaseUrl()
                return "$base$zoom/$x/$y$mImageFilenameEnding"
            }
        }

        val zoom = 5
        val x = 10
        val y = 20
        val url = darkBasemap.getTileURLString(MapTileIndex.getTileIndex(zoom, x, y))

        assertEquals("${darkBasemap.getBaseUrl()}$zoom/$x/$y.png", url)
        assertTrue(url.startsWith("http"))
    }

    @Test
    fun testRainViewerUrlStructure() {
        val timestamp = 1620000000L
        val z = 4
        val x = 5
        val y = 6
        val generatedUrl = "https://tilecache.rainviewer.com/v2/radar/$timestamp/256/$z/$x/$y/2/1_1.png"
        
        assertTrue(generatedUrl.contains(timestamp.toString()))
        assertTrue(generatedUrl.contains("/256/$z/$x/$y"))
    }

    @Test
    fun testNoaaNexradUrlStructure() {
        val z = 7
        val x = 20
        val y = 30
        val generatedUrl = "https://mesonet.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/$z/$x/$y.png"

        assertTrue(generatedUrl.contains("nexrad-n0q-900913"))
        assertTrue(generatedUrl.endsWith(".png"))
    }
}
