package org.breezyweather.ui.radar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
        assertEquals(1f, state.progress, 0.0f)
        assertTrue(state.isLoading)
    }

    @Test
    fun testInsightSeverityLevels() {
        assertEquals(3, InsightSeverity.values().size)
        assertTrue(InsightSeverity.values().contains(InsightSeverity.INFO))
        assertTrue(InsightSeverity.values().contains(InsightSeverity.WARNING))
        assertTrue(InsightSeverity.values().contains(InsightSeverity.CRITICAL))
    }
}
