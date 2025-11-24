package org.breezyweather.ui.radar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import breezyweather.domain.location.model.Location
import org.breezyweather.R
import org.breezyweather.ui.radar.composables.LayerQuickToggles
import org.breezyweather.ui.radar.composables.RadarControls

@Composable
fun InteractiveRadarCard(
    location: Location,
    modifier: Modifier = Modifier
) {
    // Simple state management without ViewModel for now
    val layers = remember { mutableStateOf(listOf(RadarLayer.PRECIPITATION)) }
    val animationState = remember { mutableStateOf(RadarAnimationState()) }
    val insights = remember {
        mutableStateOf(listOf(
            RadarInsight(0, "Heavy rain approaching", "Starting in 23 mins", InsightSeverity.WARNING),
            RadarInsight(0, "Temperature drop", "Falling 5° in next hour", InsightSeverity.INFO)
        ))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Weather Radar",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

                // Map View Container
                Box(modifier = Modifier.height(450.dp)) {
                    RadarMapView(
                        location = location,
                        layers = layers.value,
                        animationState = animationState.value,
                        onLayerToggle = { /* TODO */ }
                    )

                    // Floating controls
                    RadarControls(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        animationState = animationState.value,
                        onPlayPause = {
                            animationState.value = animationState.value.copy(
                                isPlaying = !animationState.value.isPlaying
                            )
                        },
                        onTimeSeek = { /* TODO */ }
                    )
                    
                    LayerQuickToggles(
                        modifier = Modifier.align(Alignment.TopEnd),
                        onLayerToggle = { /* Show full layer dialog */ }
                    )
                }

                // Weather Insights
                if (insights.value.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Alerts",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        insights.value.forEach { insight ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                // Severity Indicator
                                val severityColor = when(insight.severity) {
                                    InsightSeverity.CRITICAL -> colorResource(R.color.colorLevel_4)
                                    InsightSeverity.WARNING -> colorResource(R.color.colorLevel_2)
                                    InsightSeverity.INFO -> MaterialTheme.colorScheme.primary
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(severityColor, RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = insight.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = insight.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}