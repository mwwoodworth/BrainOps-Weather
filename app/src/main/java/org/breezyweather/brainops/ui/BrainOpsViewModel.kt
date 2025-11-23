package org.breezyweather.brainops.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.breezyweather.brainops.BrainOpsConfig
import org.breezyweather.brainops.repo.BrainOpsRepository

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
)

class BrainOpsViewModel(
    private val config: BrainOpsConfig
) : ViewModel() {

    private val repository = BrainOpsRepository(config)

    private val _opsImpact = MutableStateFlow<UiState<OpsImpact>>(UiState.Loading)
    val opsImpact: StateFlow<UiState<OpsImpact>> = _opsImpact

    fun loadOpsImpact() {
        if (config.apiKey.isBlank() || config.tenantId.isBlank()) {
            _opsImpact.value = UiState.Error("Missing API key or tenant")
            return
        }

        _opsImpact.value = UiState.Loading
        viewModelScope.launch {
            try {
                val weather = repository.getCurrentWeather()
                val tasks = repository.getTasks(status = null, limit = 20)
                val safety = weather.workSafety

                val impact = OpsImpact(
                    weatherStatus = weather.weather?.weather?.firstOrNull()?.main ?: "Unknown",
                    safeToWork = safety?.safeToWork ?: true,
                    workScore = safety?.score ?: 100,
                    workAlerts = safety?.alerts ?: emptyList(),
                    activeTasks = tasks.tasks.size
                )
                _opsImpact.value = UiState.Success(impact)
            } catch (e: Exception) {
                _opsImpact.value = UiState.Error(e.message ?: "Unable to load Ops Impact")
            }
        }
    }

    class Factory(private val config: BrainOpsConfig) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrainOpsViewModel(config) as T
        }
    }
}
