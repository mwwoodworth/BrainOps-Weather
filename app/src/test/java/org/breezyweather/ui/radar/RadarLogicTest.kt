package org.breezyweather.ui.radar

import org.junit.Assert.*
import org.junit.Test

class RadarLogicTest {

    @Test
    fun testRadarLayerDefinitions() {
        val precip = RadarLayer.PRECIPITATION
        assertEquals("precipitation", precip.layerId)
        assertEquals("Radar", precip.title)
        assertEquals("nexrad", precip.endpoint)
    }

    @Test
    fun testAnimationStateDefaults() {
        val state = RadarAnimationState()
        assertFalse(state.isPlaying)
        assertEquals(1.0f, state.playbackSpeed, 0.0f)
        assertEquals(0f, state.progress, 0.0f)
    }

    @Test
    fun testInsightSeverityLevels() {
        assertEquals(3, InsightSeverity.values().size)
        assertTrue(InsightSeverity.values().contains(InsightSeverity.INFO))
        assertTrue(InsightSeverity.values().contains(InsightSeverity.WARNING))
        assertTrue(InsightSeverity.values().contains(InsightSeverity.CRITICAL))
    }
}
