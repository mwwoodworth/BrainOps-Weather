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
            .putString(KEY_WEATHER_BASE_URL, config.weatherBaseUrl)
            .putString(KEY_AI_BASE_URL, config.aiBaseUrl)
            .putString(KEY_API_KEY, config.apiKey)
            .putString(KEY_TENANT_ID, config.tenantId)
            .putBoolean(KEY_ENABLE_INTEGRATION, config.enableIntegration)
            .apply()
    }

    fun loadConfig(): BrainOpsConfig {
        return BrainOpsConfig(
            weatherBaseUrl = prefs.getString(KEY_WEATHER_BASE_URL, BrainOpsConfig().weatherBaseUrl)
                ?: BrainOpsConfig().weatherBaseUrl,
            aiBaseUrl = prefs.getString(KEY_AI_BASE_URL, BrainOpsConfig().aiBaseUrl)
                ?: BrainOpsConfig().aiBaseUrl,
            apiKey = prefs.getString(KEY_API_KEY, "") ?: "",
            tenantId = prefs.getString(KEY_TENANT_ID, "") ?: "",
            enableIntegration = prefs.getBoolean(KEY_ENABLE_INTEGRATION, false)
        )
    }

    companion object {
        private const val PREF_NAME = "brainops_config"
        private const val KEY_WEATHER_BASE_URL = "weather_api_base_url"
        private const val KEY_AI_BASE_URL = "ai_api_base_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_TENANT_ID = "tenant_id"
        private const val KEY_ENABLE_INTEGRATION = "enable_integration"
    }
}
