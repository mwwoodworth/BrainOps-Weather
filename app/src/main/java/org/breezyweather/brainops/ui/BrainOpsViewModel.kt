package org.breezyweather.brainops.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.breezyweather.brainops.BrainOpsConfig
import org.breezyweather.brainops.repo.BrainOpsRepository
import org.breezyweather.brainops.auth.AuthRepository

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class OpsImpact(
    val weatherStatus: String = "Unknown",
    val safeToWork: Boolean = true,
    val workScore: Int = 100,
    val workAlerts: List<String> = emptyList(),
    val activeTasks: Int = 0,
    val agentCount: Int = 0,
    val riskLevel: String = "Low"
)

class BrainOpsViewModel(
    private val config: BrainOpsConfig,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val repository = BrainOpsRepository(config, authRepository)

    private val _opsImpact = MutableStateFlow<UiState<OpsImpact>>(UiState.Loading)
    val opsImpact: StateFlow<UiState<OpsImpact>> = _opsImpact

    fun loadOpsImpact() {
        val hasAccessToken = authRepository.getAccessToken()?.isNotBlank() == true
        val effectiveApiKey = config.apiKey.ifBlank { config.devApiKey }
        val effectiveTenant = config.effectiveTenantId
        if ((!hasAccessToken && effectiveApiKey.isBlank()) || effectiveTenant.isBlank()) {
            _opsImpact.value = UiState.Error("Missing BrainOps auth or tenant")
            return
        }

        _opsImpact.value = UiState.Loading
        viewModelScope.launch {
            try {
                val weather = repository.getCurrentWeather()
                val tasks = repository.getTasks(status = null, limit = 20)
                val agents = runCatching { repository.getAgents() }.getOrNull()
                val safety = weather.workSafety

                val safe = safety?.safeToWork ?: true
                val score = safety?.score ?: 100
                val risk = when {
                    !safe -> "High"
                    score < 50 -> "Medium"
                    tasks.tasks.size > 20 -> "Medium"
                    else -> "Low"
                }

                val impact = OpsImpact(
                    weatherStatus = weather.weather?.weather?.firstOrNull()?.main ?: "Unknown",
                    safeToWork = safe,
                    workScore = score,
                    workAlerts = safety?.alerts ?: emptyList(),
                    activeTasks = tasks.tasks.size,
                    agentCount = agents?.agents?.size ?: 0,
                    riskLevel = risk
                )
                _opsImpact.value = UiState.Success(impact)
            } catch (e: Exception) {
                _opsImpact.value = UiState.Error(e.message ?: "Unable to load Ops Impact")
            }
        }
    }

    class Factory(
        private val config: BrainOpsConfig,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrainOpsViewModel(config, authRepository) as T
        }
    }
}
