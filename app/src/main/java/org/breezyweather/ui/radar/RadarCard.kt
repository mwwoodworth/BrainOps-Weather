package org.breezyweather.ui.radar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import breezyweather.domain.location.model.Location
import org.breezyweather.R
import org.breezyweather.common.utils.helpers.IntentHelper
import org.breezyweather.ui.radar.RadarProvider
import org.breezyweather.ui.radar.RadarStatus

/**
 * Interactive radar preview card with smooth 120Hz animations.
 * Inspired by Today Weather and Overdrop's premium dark UI.
 */
@Composable
fun InteractiveRadarCard(
    location: Location,
    modifier: Modifier = Modifier,
    onExpandClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var radarStatus by remember {
        mutableStateOf(
            RadarStatus(
                provider = RadarProvider.RAINVIEWER,
                isOperational = false,
                message = "Loading radar"
            )
        )
    }

    // Pulsing animation for LIVE indicator - optimized for 120Hz
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    BrainopsOverdropCard(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            // Launch full-screen radar activity
            onExpandClick?.invoke() ?: IntentHelper.startRadarActivity(context, location)
        }
    ) {
        // Header with premium styling
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.brainops_surface_glass))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pulsing radar dot indicator
                    Box(contentAlignment = Alignment.Center) {
                        // Pulse ring effect
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .scale(pulseScale)
                                .alpha(pulseAlpha * 0.4f)
                                .background(
                                    colorResource(R.color.brainops_primary),
                                    shape = CircleShape
                                )
                        )
                        // Core dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    colorResource(R.color.brainops_primary),
                                    shape = CircleShape
                                )
                        )
                    }
                    Text(
                        text = "Live Radar",
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
                    isInteractive = false,
                    onStatusChanged = { radarStatus = it }
                )

                // Subtle gradient overlay for depth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.08f)
                                )
                            )
                        )
                )

                RadarStatusChip(
                    status = radarStatus,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )

                // Tap hint - minimal and elegant with glass effect
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(
                            color = colorResource(R.color.brainops_surface_glass),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            onExpandClick?.invoke() ?: IntentHelper.startRadarActivity(context, location)
                        }
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
                    .background(colorResource(R.color.brainops_surface_glass))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Animated pulsing live dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .alpha(pulseAlpha)
                            .background(
                                colorResource(R.color.brainops_primary),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
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
                    text = formatPreviewStatus(radarStatus),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.brainops_text_secondary)
                )
            }
        }
    }
}

@Composable
private fun BrainopsOverdropCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val borderColor = colorResource(R.color.brainops_primary).copy(alpha = 0.45f)

    Surface(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable { onClick() }
            } else {
                Modifier
            }
        ),
        shape = shape,
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colorResource(R.color.brainops_gradient_start),
                            colorResource(R.color.brainops_gradient_mid),
                            colorResource(R.color.brainops_gradient_end)
                        )
                    )
                )
                .background(colorResource(R.color.brainops_surface_glass))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun RadarStatusChip(status: RadarStatus, modifier: Modifier = Modifier) {
    val borderColor = if (status.isOperational) {
        colorResource(R.color.brainops_primary).copy(alpha = 0.6f)
    } else {
        Color(0xFFFF6B6B).copy(alpha = 0.6f)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.brainops_surface_glass),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(borderColor, CircleShape)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = when (status.provider) {
                        RadarProvider.NOAA -> "NOAA"
                        RadarProvider.RAINVIEWER -> "RainViewer"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colorResource(R.color.brainops_text_primary)
                )
                Text(
                    text = formatPreviewStatus(status),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (status.isOperational) colorResource(R.color.brainops_text_secondary)
                            else Color(0xFFFFA8A8)
                )
            }
        }
    }
}

private fun formatPreviewStatus(status: RadarStatus): String {
    val timeLabel = formatPreviewTimestamp(status.lastUpdatedEpochSeconds)
    return when {
        !status.isOperational && status.message != null -> status.message
        status.message.isNullOrBlank() -> timeLabel
        else -> "$timeLabel • ${status.message}"
    }
}

private fun formatPreviewTimestamp(epochSeconds: Long?): String {
    if (epochSeconds == null) return "Awaiting data"
    val nowSeconds = System.currentTimeMillis() / 1000
    val minutesAgo = ((nowSeconds - epochSeconds) / 60).coerceAtLeast(0)
    return when {
        minutesAgo == 0L -> "Live now"
        minutesAgo < 60 -> "${minutesAgo}m ago"
        minutesAgo < 1440 -> "${minutesAgo / 60}h ago"
        else -> "${minutesAgo / 1440}d ago"
    }
}
