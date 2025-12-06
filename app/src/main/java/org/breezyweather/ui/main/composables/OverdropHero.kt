package org.breezyweather.ui.main.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HeroUiState(
    val locationName: String,
    val condition: String?,
    val temperatureValue: Int?,
    val temperatureUnit: String?,
    val feelsLike: String?,
    val rangeLabel: String?,
    val refreshLabel: String?,
    val timezoneLabel: String?,
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OverdropHero(
    state: HeroUiState,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // Gentle animated sweep for the creamy glass header
    val sweep = rememberInfiniteTransition(label = "hero_gradient")
    val sweepOffset by sweep.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_gradient_offset"
    )

    val containerColor = colorScheme.surface.copy(alpha = 0.92f)
    val glassStroke = colorScheme.surfaceVariant.copy(alpha = 0.6f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, glassStroke),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colorScheme.surface.copy(alpha = 0.94f),
                            colorScheme.surfaceVariant.copy(alpha = 0.88f),
                            colorScheme.primary.copy(alpha = 0.14f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(
                            800f * (0.6f + sweepOffset),
                            900f
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Subtle radial glow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(150f, 120f),
                            radius = 520f
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = state.locationName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp
                            ),
                            color = colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        state.timezoneLabel?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    state.refreshLabel?.let { refresh ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = colorScheme.primary.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = refresh,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                state.condition?.let { condition ->
                    Text(
                        text = condition,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                    color = colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    val animatedTemp by animateIntAsState(
                        targetValue = state.temperatureValue ?: 0,
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                        label = "hero_temp_anim"
                    )
                    AnimatedContent(
                        targetState = animatedTemp,
                        label = "hero_temp_content"
                    ) { value ->
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 64.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.temperatureUnit.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.sp
                        ),
                        color = colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    state.feelsLike?.let { feelsLike ->
                        InfoPill(
                            iconTint = colorScheme.primary,
                            text = feelsLike,
                            color = colorScheme.surface.copy(alpha = 0.55f)
                        )
                    }
                    state.rangeLabel?.let { range ->
                        InfoPill(
                            iconTint = colorScheme.secondary,
                            text = range,
                            color = colorScheme.surface.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoPill(
    text: String,
    color: Color,
    iconTint: Color,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color,
        border = BorderStroke(1.dp, iconTint.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.8f))
                    .alpha(0.9f)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
