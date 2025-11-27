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
    primary = BrainOpsBlueSecondary,
    onPrimary = Color(0xFF002F67),
    primaryContainer = Color(0xFF004591),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = BrainOpsBluePrimary,
    onSecondary = Color(0xFF003542),
    secondaryContainer = Color(0xFF004E60),
    onSecondaryContainer = Color(0xFFB1EBFF),
    tertiary = BrainOpsTealAccent,
    onTertiary = Color(0xFF3F2E00),
    tertiaryContainer = Color(0xFF5A4300),
    onTertiaryContainer = Color(0xFFFFDF8E),
    background = BrainOpsDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = BrainOpsDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = BrainOpsDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    inverseOnSurface = Color(0xFF0F172A),
    inverseSurface = Color(0xFFF1F5F9),
    inversePrimary = Color(0xFF4E86F6),
    error = BrainOpsError,
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)

private val LightColorScheme = lightColorScheme(
    primary = BrainOpsBluePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001B3F),
    secondary = BrainOpsBlueSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB1EBFF),
    onSecondaryContainer = Color(0xFF001F28),
    tertiary = BrainOpsTealAccent,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDF8E),
    onTertiaryContainer = Color(0xFF251A00),
    background = BrainOpsLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = BrainOpsLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = BrainOpsLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8),
    inverseOnSurface = Color(0xFFF1F5F9),
    inverseSurface = Color(0xFF334155),
    inversePrimary = Color(0xFFA8C7FF),
    error = BrainOpsError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
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
