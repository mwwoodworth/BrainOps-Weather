package org.breezyweather.ui.radar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import breezyweather.domain.location.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(
    // private val radarRepository: RadarRepository
) : ViewModel() {

    private val _activeLayers = MutableStateFlow<List<RadarLayer>>(listOf(RadarLayer.PRECIPITATION))
    val activeLayers: StateFlow<List<RadarLayer>> = _activeLayers.asStateFlow()

    private val _animationState = MutableStateFlow(RadarAnimationState())
    val animationState: StateFlow<RadarAnimationState> = _animationState.asStateFlow()
    
    private val _insights = MutableStateFlow<List<RadarInsight>>(emptyList())
    val insights: StateFlow<List<RadarInsight>> = _insights.asStateFlow()

    init {
        // Simulate fetching insights
        viewModelScope.launch {
            delay(1000)
            _insights.value = listOf(
                RadarInsight(0, "Heavy Rain Approaching", "Starting in 23 mins", InsightSeverity.WARNING),
                RadarInsight(0, "Temperature Drop", "Dropping 5°F in 1 hour", InsightSeverity.INFO)
            )
        }
    }

    fun toggleLayer(layer: RadarLayer) {
        val current = _activeLayers.value.toMutableList()
        if (current.contains(layer)) {
            current.remove(layer)
        } else {
            current.add(layer)
        }
        _activeLayers.value = current
    }

    fun toggleAnimation() {
        val currentState = _animationState.value
        if (currentState.isPlaying) {
            pauseAnimation()
        } else {
            playAnimation()
        }
    }

    fun playAnimation() {
        viewModelScope.launch {
            _animationState.value = _animationState.value.copy(isPlaying = true)
            // Simple animation loop simulation
            while (_animationState.value.isPlaying) {
                delay(100) // 10fps for now
                val newProgress = (_animationState.value.progress + 0.05f) % 1.0f
                _animationState.value = _animationState.value.copy(progress = newProgress)
            }
        }
    }

    fun pauseAnimation() {
        _animationState.value = _animationState.value.copy(isPlaying = false)
    }

    fun seekToTime(timestamp: Long) {
        // Logic to map timestamp to progress
    }
}
