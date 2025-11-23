package org.breezyweather.brainops.repo

import org.breezyweather.brainops.BrainOpsConfig
import org.breezyweather.brainops.api.BrainOpsApiClient
import org.breezyweather.brainops.api.TasksResponse
import org.breezyweather.brainops.api.WeatherApiClient
import org.breezyweather.brainops.api.WeatherResponse
import org.breezyweather.brainops.auth.AuthRepository
import org.breezyweather.brainops.api.AgentsApiClient
import org.breezyweather.brainops.api.AgentsResponse

class BrainOpsRepository(
    private val config: BrainOpsConfig,
    private val authRepository: AuthRepository
) {
    private val weatherApi = WeatherApiClient(config) { authRepository.getAccessToken() }.api
    private val brainOpsApi = BrainOpsApiClient(config) { authRepository.getAccessToken() }.api
    private val agentsApi = AgentsApiClient(config) { authRepository.getAccessToken() }.api

    private fun effectiveApiKey(): String =
        config.apiKey.ifBlank { config.devApiKey }

    suspend fun getCurrentWeather(): WeatherResponse {
        val auth = "Bearer ${effectiveApiKey()}"
        val tenant = config.effectiveTenantId
        val response = weatherApi.getCurrentWeather(auth, tenant)
        if (response.isSuccessful) {
            return response.body() ?: WeatherResponse()
        }
        throw IllegalStateException("Weather fetch failed: ${response.code()}")
    }

    suspend fun getTasks(status: String? = null, limit: Int = 10): TasksResponse {
        val auth = "Bearer ${effectiveApiKey()}"
        val tenant = config.effectiveTenantId
        val response = brainOpsApi.getTasks(auth, tenant, tenant, status, limit)
        if (response.isSuccessful) {
            return response.body() ?: TasksResponse()
        }
        throw IllegalStateException("Tasks fetch failed: ${response.code()}")
    }

    suspend fun getAgents(): AgentsResponse {
        val auth = "Bearer ${effectiveApiKey()}"
        val response = agentsApi.getAgents(auth)
        if (response.isSuccessful) {
            return response.body() ?: AgentsResponse()
        }
        throw IllegalStateException("Agents fetch failed: ${response.code()}")
    }
}
