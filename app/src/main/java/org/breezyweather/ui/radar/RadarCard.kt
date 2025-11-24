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
            RadarInsight(0, "Heavy Rain Approaching", "Starting in 23 mins", InsightSeverity.WARNING),
            RadarInsight(0, "Temperature Drop", "Dropping 5°F in 1 hour", InsightSeverity.INFO)
        ))
    }

    val gradientStart = colorResource(R.color.brainops_gradient_start)
    val gradientEnd = colorResource(R.color.brainops_gradient_end)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // To show gradient
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientStart, gradientEnd)
                    )
                )
        ) {
            Column {
                // Header with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_brainops_radar),
                        contentDescription = null,
                        tint = colorResource(R.color.brainops_primary),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "INTERACTIVE RADAR", // Uppercase for tech look
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color.White
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

                // AI Insights Panel
                if (insights.value.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(R.color.brainops_surface_glass))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "BRAINOPS INSIGHTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorResource(R.color.brainops_secondary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        insights.value.forEach { insight ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                // Severity Indicator
                                val severityColor = when(insight.severity) {
                                    InsightSeverity.CRITICAL -> Color.Red
                                    InsightSeverity.WARNING -> Color(0xFFFFD600)
                                    InsightSeverity.INFO -> Color(0xFF00E5FF)
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
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = insight.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}