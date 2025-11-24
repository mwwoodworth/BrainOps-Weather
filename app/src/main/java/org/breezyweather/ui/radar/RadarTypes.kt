package org.breezyweather.ui.radar

enum class RadarLayer(val layerId: String, val title: String, val endpoint: String) {
    PRECIPITATION("precipitation", "Precipitation", "precipitation_new"),
    CLOUDS("clouds", "Clouds", "clouds_new"),
    TEMPERATURE("temperature", "Temperature", "temp_new"),
    WIND("wind", "Wind", "wind_new"),
    PRESSURE("pressure", "Pressure", "pressure_new")
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
