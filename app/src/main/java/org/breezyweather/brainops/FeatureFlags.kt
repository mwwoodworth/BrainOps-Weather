package org.breezyweather.brainops

import org.breezyweather.BuildConfig
object FeatureFlags {
    const val BRAINOPS_INTEGRATION = "brainops_integration"
    const val BRAINOPS_CARDS = "brainops_cards"
    const val GOOGLE_TILES = "google_tiles"

    fun isBrainOpsEnabled(config: BrainOpsConfig): Boolean {
        return config.enableIntegration || BuildConfig.DEBUG
    }
}
