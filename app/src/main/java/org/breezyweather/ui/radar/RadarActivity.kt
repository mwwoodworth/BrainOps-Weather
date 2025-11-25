package org.breezyweather.ui.radar

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import breezyweather.domain.location.model.Location
import kotlinx.coroutines.delay
import org.breezyweather.R
import org.breezyweather.ui.radar.composables.RadarControls
import org.breezyweather.ui.theme.compose.BreezyWeatherTheme

class RadarActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge and hardware acceleration for smooth 120Hz rendering
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        // Get location from intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val locationName = intent.getStringExtra("locationName") ?: "Current Location"

        // Create location object
        val location = Location(
            cityId = "",
            latitude = latitude,
            longitude = longitude,
            timeZone = java.util.TimeZone.getDefault(),
            country = "",
            countryCode = "",
            admin1 = "",
            admin1Code = "",
            city = locationName
        )

        setContent {
            BreezyWeatherTheme {
                FullScreenRadarScreen(
                    location = location,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenRadarScreen(
    location: Location,
    onBackClick: () -> Unit
) {
    var selectedLayers by remember { mutableStateOf(listOf(RadarLayer.PRECIPITATION)) }
    var animationState by remember { mutableStateOf(RadarAnimationState()) }
    var showLayerSelector by remember { mutableStateOf(false) }
    var radarTimestamps by remember { mutableStateOf<List<Long>>(emptyList()) }

    // Smooth animation with proper frame timing for 120Hz displays
    // Each radar frame represents ~10 minutes, we animate at ~500ms per frame for natural playback
    val animationFrameDelay = (500L / animationState.playbackSpeed).toLong().coerceAtLeast(100L)

    LaunchedEffect(animationState.isPlaying, animationState.playbackSpeed) {
        if (animationState.isPlaying && radarTimestamps.isNotEmpty()) {
            while (animationState.isPlaying) {
                delay(animationFrameDelay)
                val currentIndex = animationState.currentFrameIndex
                val nextIndex = if (currentIndex >= radarTimestamps.size - 1) 0 else currentIndex + 1
                val newProgress = nextIndex.toFloat() / (radarTimestamps.size - 1).coerceAtLeast(1)

                animationState = animationState.copy(
                    progress = newProgress,
                    currentFrameIndex = nextIndex,
                    currentTimestamp = radarTimestamps.getOrNull(nextIndex) ?: System.currentTimeMillis()
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Weather Radar",
                            color = colorResource(R.color.brainops_text_primary),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = location.city,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(R.color.brainops_text_secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorResource(R.color.brainops_text_primary)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showLayerSelector = !showLayerSelector }) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Layers",
                            tint = if (showLayerSelector) colorResource(R.color.brainops_primary)
                                   else colorResource(R.color.brainops_text_primary)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.darkPrimary_5)
                )
            )
        },
        containerColor = colorResource(R.color.darkPrimary_5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Full-screen interactive map with timestamp callback
            RadarMapView(
                location = location,
                layers = selectedLayers,
                animationState = animationState,
                onLayerToggle = {},
                isInteractive = true,
                modifier = Modifier.fillMaxSize(),
                onTimestampsLoaded = { timestamps ->
                    radarTimestamps = timestamps
                    if (timestamps.isNotEmpty()) {
                        animationState = animationState.copy(
                            availableTimestamps = timestamps,
                            currentFrameIndex = timestamps.size - 1,
                            progress = 1f,
                            isLoading = false
                        )
                    }
                }
            )

            // Layer selector overlay
            if (showLayerSelector) {
                LayerSelectorOverlay(
                    selectedLayers = selectedLayers,
                    onLayersChanged = { selectedLayers = it },
                    onDismiss = { showLayerSelector = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }

            // Bottom controls with proper seek handling
            RadarControls(
                modifier = Modifier.align(Alignment.BottomCenter),
                animationState = animationState,
                onPlayPause = {
                    animationState = animationState.copy(
                        isPlaying = !animationState.isPlaying
                    )
                },
                onTimeSeek = { progress ->
                    if (radarTimestamps.isNotEmpty()) {
                        val frameIndex = (progress * (radarTimestamps.size - 1)).toInt()
                            .coerceIn(0, radarTimestamps.size - 1)
                        animationState = animationState.copy(
                            progress = progress,
                            currentFrameIndex = frameIndex,
                            currentTimestamp = radarTimestamps.getOrNull(frameIndex) ?: System.currentTimeMillis(),
                            isPlaying = false // Pause when user seeks
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun LayerSelectorOverlay(
    selectedLayers: List<RadarLayer>,
    onLayersChanged: (List<RadarLayer>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.darkPrimary_4),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            colorResource(R.color.brainops_primary),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Text(
                    text = "Radar Layers",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    ),
                    color = colorResource(R.color.brainops_text_primary)
                )
            }

            RadarLayer.values().forEach { layer ->
                val isSelected = selectedLayers.contains(layer)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) colorResource(R.color.darkPrimary_5)
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = layer.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) colorResource(R.color.brainops_primary)
                               else colorResource(R.color.brainops_text_secondary)
                    )
                    Switch(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            onLayersChanged(
                                if (checked) selectedLayers + layer
                                else selectedLayers - layer
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorResource(R.color.brainops_primary),
                            checkedTrackColor = colorResource(R.color.brainops_primary).copy(alpha = 0.4f),
                            uncheckedThumbColor = colorResource(R.color.brainops_text_secondary),
                            uncheckedTrackColor = colorResource(R.color.darkPrimary_3)
                        )
                    )
                }
            }
        }
    }
}
