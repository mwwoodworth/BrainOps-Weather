package org.breezyweather.ui.radar

enum class RadarLayer(val layerId: String, val title: String, val endpoint: String) {
    PRECIPITATION("precipitation", "Radar", "nexrad")
    // Uses NOAA NEXRAD for US locations (free forever)
    // Uses RainViewer for international locations (free)
    // No API key required for either source
}

enum class RadarProvider {
    NOAA,
    RAINVIEWER
}

data class RadarStatus(
    val provider: RadarProvider,
    val lastUpdatedEpochSeconds: Long? = null,
    val isOperational: Boolean = true,
    val message: String? = null
)

/**
 * Animation state for radar playback.
 * Optimized for smooth 120Hz+ display rendering.
 */
data class RadarAnimationState(
    val isPlaying: Boolean = false,
    val currentTimestamp: Long = System.currentTimeMillis(),
    val playbackSpeed: Float = 1.0f, // 1x, 2x speed options
    val progress: Float = 1f, // 0.0 to 1.0 (1.0 = LIVE/most recent)
    val availableTimestamps: List<Long> = emptyList(),
    val currentFrameIndex: Int = -1,
    val isLoading: Boolean = true
) {
    /**
     * Get formatted time label for current frame (e.g., "-45 min" or "LIVE")
     */
    fun getTimeLabel(): String {
        if (availableTimestamps.isEmpty() || currentFrameIndex < 0) return "LIVE"

        val latestTimestamp = availableTimestamps.lastOrNull() ?: return "LIVE"
        val currentTs = if (currentFrameIndex < availableTimestamps.size) {
            availableTimestamps[currentFrameIndex]
        } else {
            latestTimestamp
        }

        val diffMinutes = ((latestTimestamp - currentTs) / 60).toInt()
        return when {
            diffMinutes <= 0 -> "LIVE"
            diffMinutes < 60 -> "-${diffMinutes}m"
            else -> "-${diffMinutes / 60}h ${diffMinutes % 60}m"
        }
    }

    /**
     * Check if currently showing the live (most recent) frame
     */
    fun isLive(): Boolean {
        return availableTimestamps.isEmpty() ||
               currentFrameIndex < 0 ||
               currentFrameIndex >= availableTimestamps.size - 1 ||
               progress >= 0.99f
    }
}

data class RadarInsight(
    val iconRes: Int, // Placeholder int for resource ID
    val title: String,
    val description: String,
    val severity: InsightSeverity
)

enum class InsightSeverity {
    INFO, WARNING, CRITICAL
}

/**
 * Animation speed options for radar playback
 */
enum class PlaybackSpeed(val multiplier: Float, val label: String) {
    NORMAL(1.0f, "1x"),
    FAST(2.0f, "2x"),
    SLOW(0.5f, "0.5x")
}
