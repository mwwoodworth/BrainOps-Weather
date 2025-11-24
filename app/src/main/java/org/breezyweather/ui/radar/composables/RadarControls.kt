package org.breezyweather.ui.radar.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.breezyweather.R
import org.breezyweather.ui.radar.RadarAnimationState

@Composable
fun RadarControls(
    animationState: RadarAnimationState,
    onPlayPause: () -> Unit,
    onTimeSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColor = colorResource(R.color.brainops_surface_glass)
    val accentColor = colorResource(R.color.brainops_primary)
    val borderColor = colorResource(R.color.brainops_secondary).copy(alpha = 0.5f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(72.dp), // Fixed height for touch target
        color = glassColor,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Play/Pause Button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (animationState.isPlaying) R.drawable.ic_brainops_pause 
                        else R.drawable.ic_brainops_play
                    ),
                    contentDescription = if (animationState.isPlaying) "Pause" else "Play",
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Timeline Slider (Customized)
            Slider(
                value = animationState.progress,
                onValueChange = { newProgress ->
                    val now = System.currentTimeMillis()
                    // consistent with RadarActivity: progress 1.0 is LIVE, 0.0 is -1h
                    val timestamp = now - ((1f - newProgress) * 3600000).toLong()
                    onTimeSeek(timestamp)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
            
            // Live Badge
            Surface(
                color = accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor)
            ) {
                Text(
                    text = "LIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
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