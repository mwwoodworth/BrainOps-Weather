package org.breezyweather.brainops

data class BrainOpsConfig(
    val apiBaseUrl: String = "https://brainops-backend-prod.onrender.com",
    val apiKey: String = "",
    val tenantId: String = "",
    val enableIntegration: Boolean = false
)
