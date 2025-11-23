package org.breezyweather.brainops.repo

import org.breezyweather.brainops.BrainOpsConfig
import org.breezyweather.brainops.api.BrainOpsApiClient
import org.breezyweather.brainops.api.TasksResponse
import org.breezyweather.brainops.api.WeatherApiClient
import org.breezyweather.brainops.api.WeatherResponse

class BrainOpsRepository(private val config: BrainOpsConfig) {
    private val weatherApi = WeatherApiClient(config).api
    private val brainOpsApi = BrainOpsApiClient(config).api

    suspend fun getCurrentWeather(): WeatherResponse {
        val auth = "Bearer ${config.apiKey}"
        val tenant = config.tenantId
        val response = weatherApi.getCurrentWeather(auth, tenant)
        if (response.isSuccessful) {
            return response.body() ?: WeatherResponse()
        }
        throw IllegalStateException("Weather fetch failed: ${response.code()}")
    }

    suspend fun getTasks(status: String? = null, limit: Int = 10): TasksResponse {
        val auth = "Bearer ${config.apiKey}"
        val tenant = config.tenantId
        val response = brainOpsApi.getTasks(auth, tenant, config.tenantId, status, limit)
        if (response.isSuccessful) {
            return response.body() ?: TasksResponse()
        }
        throw IllegalStateException("Tasks fetch failed: ${response.code()}")
    }
}
