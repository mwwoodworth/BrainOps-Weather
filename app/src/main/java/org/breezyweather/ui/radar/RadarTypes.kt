package org.breezyweather.ui.radar

enum class RadarLayer(val layerId: String, val title: String, val endpoint: String) {
    PRECIPITATION("precipitation", "Radar", "nexrad")
    // Uses NOAA NEXRAD for US locations (free forever)
    // Uses RainViewer for international locations (free)
    // No API key required for either source
}

data class RadarAnimationState(
    val isPlaying: Boolean = false,
    val currentTimestamp: Long = System.currentTimeMillis(),
    val playbackSpeed: Float = 1.0f, // 1x speed
    val progress: Float = 0f // 0.0 to 1.0
)

data class RadarInsight(
    val iconRes: Int, // Placeholder int for resource ID
    val title: String,
    val description: String,
    val severity: InsightSeverity
)

enum class InsightSeverity {
    INFO, WARNING, CRITICAL
}
