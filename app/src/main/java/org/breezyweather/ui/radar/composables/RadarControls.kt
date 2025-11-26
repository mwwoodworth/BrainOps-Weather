package org.breezyweather.ui.radar.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.breezyweather.R
import org.breezyweather.ui.radar.RadarAnimationState

/**
 * Premium radar controls with smooth 120Hz animations.
 * Inspired by Today Weather and Overdrop's clean dark UI.
 */
@Composable
fun RadarControls(
    animationState: RadarAnimationState,
    onPlayPause: () -> Unit,
    onTimeSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColor = colorResource(R.color.brainops_surface_glass)
    val accentColor = colorResource(R.color.brainops_primary)
    val borderColor = colorResource(R.color.brainops_secondary).copy(alpha = 0.3f)
    val isLive = animationState.isLive()

    // Pulsing animation for LIVE indicator
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(68.dp),
        color = glassColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play/Pause Button with ripple effect
            FilledIconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = accentColor.copy(alpha = 0.15f)
                )
            ) {
                Icon(
                    painter = painterResource(
                        if (animationState.isPlaying) R.drawable.ic_brainops_pause
                        else R.drawable.ic_brainops_play
                    ),
                    contentDescription = if (animationState.isPlaying) "Pause" else "Play",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Time Label
            Text(
                text = animationState.getTimeLabel(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = if (isLive) accentColor else colorResource(R.color.brainops_text_secondary),
                modifier = Modifier.width(48.dp)
            )

            // Timeline Slider - Smooth with custom styling
            Slider(
                value = animationState.progress,
                onValueChange = onTimeSeek,
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp),
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = colorResource(R.color.darkPrimary_3).copy(alpha = 0.6f)
                )
            )

            // Live Badge with pulse animation
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Pulse background (only when live)
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(pulseScale)
                            .alpha(pulseAlpha * 0.3f)
                            .background(accentColor, CircleShape)
                    )
                }

                Surface(
                    color = if (isLive) accentColor.copy(alpha = 0.25f)
                            else colorResource(R.color.darkPrimary_3),
                    shape = RoundedCornerShape(10.dp),
                    border = if (isLive) BorderStroke(1.dp, accentColor) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        // Pulsing dot indicator
                        if (isLive) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .alpha(pulseAlpha)
                                    .background(accentColor, CircleShape)
                            )
                        }
                        Text(
                            text = if (isLive) "LIVE" else "PAST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isLive) accentColor
                                    else colorResource(R.color.brainops_text_secondary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LayerQuickToggles(
    modifier: Modifier = Modifier,
    onLayerToggle: () -> Unit
) {
    val accentColor = colorResource(R.color.brainops_primary)
    val glassColor = colorResource(R.color.brainops_surface_glass)

    FilledIconButton(
        onClick = onLayerToggle,
        modifier = modifier.padding(16.dp).size(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = glassColor
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_brainops_layers),
            contentDescription = "Layers",
            tint = accentColor,
            modifier = Modifier.size(28.dp)
        )
    }
}
