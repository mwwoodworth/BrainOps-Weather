package org.breezyweather.brainops

import org.breezyweather.BuildConfig

data class BrainOpsConfig(
    val apiBaseUrl: String = "https://brainops-backend-prod.onrender.com/",
    val weatherBaseUrl: String = "https://weathercraft-erp.vercel.app/api/",
    val aiBaseUrl: String = "https://brainops-ai-agents.onrender.com/",
    val apiKey: String = "",
    val tenantId: String = "",
    val enableIntegration: Boolean = false,
    val radarUrl: String = ""
) {
    /**
     * Convenience accessor for the build-time dev API key.
     * Used as a fallback in debug builds when no explicit key is configured.
     */
    val devApiKey: String
        get() = BuildConfig.DEV_API_KEY

    /**
     * Returns the tenant id, falling back to the build-time default when blank.
     */
    val effectiveTenantId: String
        get() = tenantId.ifBlank { BuildConfig.DEFAULT_TENANT_ID }

    /**
     * Returns the radar URL, allowing a sensible default when none is configured.
     *
     * The default points to a generic RainViewer web radar. You can override this
     * in BrainOps settings to use your own tiles or proxy.
     */
    val effectiveRadarUrl: String
        get() = radarUrl.ifBlank {
            "https://www.rainviewer.com/weather-radar-map.html?loc=38.8339,-104.8214,7&oFa=1&c=3&oC=1&lang=en-US&lay=radar&sm=1&sn=1&hu=false"
        }
}
