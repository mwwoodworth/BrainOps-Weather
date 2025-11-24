package org.breezyweather.ui.radar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import breezyweather.domain.location.model.Location
import org.breezyweather.R
import org.breezyweather.common.utils.helpers.IntentHelper

@Composable
fun InteractiveRadarCard(
    location: Location,
    modifier: Modifier = Modifier,
    onExpandClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // Launch full-screen radar activity
                onExpandClick?.invoke() ?: IntentHelper.startRadarActivity(context, location)
            },
        elevation = CardDefaults.elevatedCardElevation(0.dp), // Flat for modern look
        shape = RoundedCornerShape(20.dp), // Larger radius like Overdrop
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.darkPrimary_5) // Deep dark background
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with premium styling
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.darkPrimary_4)) // Slightly lighter header
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Radar icon indicator
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                colorResource(R.color.brainops_primary),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                    Text(
                        text = "Weather Radar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colorResource(R.color.brainops_text_primary)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ZoomOutMap,
                    contentDescription = "Expand",
                    tint = colorResource(R.color.brainops_primary),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Radar map preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                RadarMapView(
                    location = location,
                    layers = listOf(RadarLayer.PRECIPITATION),
                    animationState = RadarAnimationState(),
                    onLayerToggle = {},
                    isInteractive = false
                )

                // Subtle gradient overlay for depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.05f)
                                )
                            )
                        )
                )

                // Tap hint - minimal and elegant
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = colorResource(R.color.brainops_surface_glass),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOutMap,
                            contentDescription = null,
                            tint = colorResource(R.color.brainops_primary),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Tap for full radar",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = colorResource(R.color.brainops_text_primary)
                        )
                    }
                }
            }

            // Status bar - sleek and informative
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.darkPrimary_4))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing live indicator
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                colorResource(R.color.brainops_primary),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorResource(R.color.brainops_primary)
                    )
                    Text(
                        text = "Precipitation",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorResource(R.color.brainops_text_secondary)
                    )
                }
                Text(
                    text = "Updated now",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.brainops_text_secondary)
                )
            }
        }
    }
}