package org.breezyweather.brainops

data class BrainOpsConfig(
    val weatherBaseUrl: String = "https://weathercraft-erp.vercel.app/api/",
    val aiBaseUrl: String = "https://brainops-ai-agents.onrender.com/",
    val apiKey: String = "brainops_dev_key_2025",
    val tenantId: String = "51e728c5-94e8-4ae0-8a0a-6a08d1fb3457",
    val enableIntegration: Boolean = false
)
