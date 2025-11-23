package org.breezyweather.brainops

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Stores BrainOps config in encrypted preferences with a safe fallback.
 */
class BrainOpsConfigStore(context: Context) {
    private val prefs = runCatching {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        // Fallback to normal preferences if encryption fails (should not happen in prod)
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveConfig(config: BrainOpsConfig) {
        prefs.edit()
            .putString(KEY_API_BASE_URL, config.apiBaseUrl)
            .putString(KEY_WEATHER_BASE_URL, config.weatherBaseUrl)
            .putString(KEY_AI_BASE_URL, config.aiBaseUrl)
            .putString(KEY_API_KEY, config.apiKey)
            .putString(KEY_TENANT_ID, config.tenantId)
            .putBoolean(KEY_ENABLE_INTEGRATION, config.enableIntegration)
            .putString(KEY_RADAR_URL, config.radarUrl)
            .apply()
    }

    fun loadConfig(): BrainOpsConfig {
        val defaults = BrainOpsConfig()
        return BrainOpsConfig(
            apiBaseUrl = prefs.getString(KEY_API_BASE_URL, defaults.apiBaseUrl)
                ?: defaults.apiBaseUrl,
            weatherBaseUrl = prefs.getString(KEY_WEATHER_BASE_URL, defaults.weatherBaseUrl)
                ?: defaults.weatherBaseUrl,
            aiBaseUrl = prefs.getString(KEY_AI_BASE_URL, defaults.aiBaseUrl)
                ?: defaults.aiBaseUrl,
            apiKey = prefs.getString(KEY_API_KEY, defaults.apiKey) ?: defaults.apiKey,
            tenantId = prefs.getString(KEY_TENANT_ID, defaults.tenantId) ?: defaults.tenantId,
            enableIntegration = prefs.getBoolean(KEY_ENABLE_INTEGRATION, defaults.enableIntegration),
            radarUrl = prefs.getString(KEY_RADAR_URL, defaults.radarUrl) ?: defaults.radarUrl
        )
    }

    companion object {
        private const val PREF_NAME = "brainops_config"
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_WEATHER_BASE_URL = "weather_api_base_url"
        private const val KEY_AI_BASE_URL = "ai_api_base_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_TENANT_ID = "tenant_id"
        private const val KEY_ENABLE_INTEGRATION = "enable_integration"
        private const val KEY_RADAR_URL = "radar_url"
    }
}
