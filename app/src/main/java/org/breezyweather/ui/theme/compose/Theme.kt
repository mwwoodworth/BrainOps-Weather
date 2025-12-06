/*
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.ui.theme.compose

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.breezyweather.R

private val DarkColorScheme = darkColorScheme(
    primary = BrainOpsBluePrimary,
    onPrimary = Color(0xFF04121B),
    primaryContainer = Color(0xFF123748),
    onPrimaryContainer = Color(0xFFC8EEFF),
    secondary = BrainOpsBlueSecondary,
    onSecondary = Color(0xFF1E1202),
    secondaryContainer = Color(0xFF3C2C16),
    onSecondaryContainer = Color(0xFFFFE5C4),
    tertiary = BrainOpsTealAccent,
    onTertiary = Color(0xFF001913),
    tertiaryContainer = Color(0xFF0F3A2D),
    onTertiaryContainer = Color(0xFFC3FFE6),
    background = BrainOpsDarkBackground,
    onBackground = Color(0xFFE7ECF4),
    surface = BrainOpsDarkSurface,
    onSurface = Color(0xFFE7ECF4),
    surfaceVariant = BrainOpsDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFA2B4CC),
    outline = Color(0xFF2D3649),
    inverseOnSurface = Color(0xFF0B1018),
    inverseSurface = Color(0xFFE7ECF4),
    inversePrimary = Color(0xFF4ABBEA),
    error = BrainOpsError,
    onError = Color(0xFF2B0000),
    errorContainer = Color(0xFF3C0A0A),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = BrainOpsBluePrimary,
    onPrimary = Color(0xFF04121B),
    primaryContainer = Color(0xFF123748),
    onPrimaryContainer = Color(0xFFC8EEFF),
    secondary = BrainOpsBlueSecondary,
    onSecondary = Color(0xFF1E1202),
    secondaryContainer = Color(0xFF3C2C16),
    onSecondaryContainer = Color(0xFFFFE5C4),
    tertiary = BrainOpsTealAccent,
    onTertiary = Color(0xFF001913),
    tertiaryContainer = Color(0xFF0F3A2D),
    onTertiaryContainer = Color(0xFFC3FFE6),
    background = BrainOpsLightBackground,
    onBackground = Color(0xFFE7ECF4),
    surface = BrainOpsLightSurface,
    onSurface = Color(0xFFE7ECF4),
    surfaceVariant = BrainOpsLightSurfaceVariant,
    onSurfaceVariant = Color(0xFFA2B4CC),
    outline = Color(0xFF2D3649),
    inverseOnSurface = Color(0xFF0B1018),
    inverseSurface = Color(0xFFE7ECF4),
    inversePrimary = Color(0xFF4ABBEA),
    error = BrainOpsError,
    onError = Color(0xFF2B0000),
    errorContainer = Color(0xFF3C0A0A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun BreezyWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val allowDynamicColor = dynamicColor &&
        context.resources.getBoolean(R.bool.enable_dynamic_color)

    val colorScheme = when {
        allowDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}

@Composable
fun themeRipple(
    bounded: Boolean = true,
) = ripple(
    color = MaterialTheme.colorScheme.primary,
    bounded = bounded
)
