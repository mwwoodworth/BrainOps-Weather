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

package org.breezyweather.ui.main.adapters.main.holder

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
import androidx.compose.ui.unit.dp
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import breezyweather.domain.location.model.Location
import org.breezyweather.R
import org.breezyweather.common.extensions.formatMeasure
import org.breezyweather.common.extensions.getRelativeTime
import org.breezyweather.domain.settings.SettingsManager
import org.breezyweather.domain.weather.model.getTemperatureRangeSummary
import org.breezyweather.ui.main.composables.HeroUiState
import org.breezyweather.ui.main.composables.OverdropHero
import org.breezyweather.ui.theme.compose.BreezyWeatherTheme
import org.breezyweather.ui.theme.resource.providers.ResourceProvider
import org.breezyweather.unit.formatting.UnitWidth
import kotlin.math.roundToInt

class HeaderViewHolder(parent: ViewGroup) : AbstractMainViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_header, parent, false)
) {

    private val composeView: ComposeView = itemView.findViewById(R.id.composeHeader)

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        context: Context,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
    ) {
        super.onBindView(context, location, provider, listAnimationEnabled, itemAnimationEnabled)

        val temperatureUnit = SettingsManager.getInstance(context).getTemperatureUnit(context)
        val temperatureValue = location.weather?.current?.temperature?.temperature
            ?.toDouble(temperatureUnit)?.roundToInt()

        val conditionText = location.weather?.current?.weatherText

        val feelsLikeLabel = location.weather?.current?.temperature?.feelsLikeTemperature?.let { feelsLike ->
            context.getString(
                R.string.temperature_feels_like_with_unit,
                feelsLike.formatMeasure(
                    context,
                    temperatureUnit,
                    valueWidth = UnitWidth.NARROW,
                    unitWidth = UnitWidth.NARROW
                )
            )
        }

        val rangeLabel = location.weather
            ?.getTemperatureRangeSummary(context, location, temperatureUnit)
            ?.first

        val refreshLabel = location.weather?.base?.refreshTime?.getRelativeTime(context)

        val locationLabel = location.customName
            ?: location.city.takeIf { it.isNotBlank() }
            ?: context.getString(R.string.app_name)

        val heroState = HeroUiState(
            locationName = locationLabel,
            condition = conditionText,
            temperatureValue = temperatureValue,
            temperatureUnit = temperatureUnit.getNominativeUnit(context),
            feelsLike = feelsLikeLabel,
            rangeLabel = rangeLabel,
            refreshLabel = refreshLabel,
            timezoneLabel = if (shouldShowDebugInfo(context)) location.timeZone.id else null
        )

        composeView.setViewCompositionStrategy(DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            BreezyWeatherTheme(darkTheme = isNightMode(context)) {
                OverdropHero(
                    state = heroState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                )
            }
        }
    }

    override fun getEnterAnimator(pendingAnimatorList: List<Animator>): Animator {
        val a: Animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f)
        a.duration = 300
        a.startDelay = 100
        a.interpolator = FastOutSlowInInterpolator()
        return a
    }

    override fun onEnterScreen() {
        super.onEnterScreen()
    }

    private fun isNightMode(context: Context): Boolean {
        val currentMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun shouldShowDebugInfo(context: Context): Boolean =
        org.breezyweather.BreezyWeather.instance.debugMode
}
