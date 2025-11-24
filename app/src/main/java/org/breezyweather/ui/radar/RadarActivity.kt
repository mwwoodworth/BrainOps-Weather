package org.breezyweather.ui.radar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import breezyweather.domain.location.model.Location
import kotlinx.coroutines.delay
import org.breezyweather.R
import org.breezyweather.ui.radar.composables.RadarControls
import org.breezyweather.ui.theme.compose.BreezyWeatherTheme

class RadarActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    // Animation logic
    LaunchedEffect(animationState.isPlaying) {
        if (animationState.isPlaying) {
            while (animationState.isPlaying) {
                delay(1000L) // Update every second
                animationState = animationState.copy(
                    progress = if (animationState.progress >= 1f) 0f else animationState.progress + 0.05f,
                    currentTimestamp = System.currentTimeMillis() - ((1f - animationState.progress) * 3600000).toLong()
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
            // Full-screen interactive map
            RadarMapView(
                location = location,
                layers = selectedLayers,
                animationState = animationState,
                onLayerToggle = {},
                isInteractive = true,
                modifier = Modifier.fillMaxSize()
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

            // Bottom controls
            RadarControls(
                modifier = Modifier.align(Alignment.BottomCenter),
                animationState = animationState,
                onPlayPause = {
                    animationState = animationState.copy(
                        isPlaying = !animationState.isPlaying
                    )
                },
                onTimeSeek = { timestamp ->
                    animationState = animationState.copy(
                        currentTimestamp = timestamp,
                        progress = (System.currentTimeMillis() - timestamp).toFloat() / 3600000f
                    )
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
