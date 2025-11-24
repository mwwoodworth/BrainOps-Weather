package org.breezyweather.ui.main.adapters.main.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import breezyweather.domain.location.model.Location
import org.breezyweather.R
import org.breezyweather.common.activities.BreezyActivity
import org.breezyweather.ui.radar.InteractiveRadarCard
import org.breezyweather.ui.theme.resource.providers.ResourceProvider

class InteractiveRadarCardViewHolder(parent: ViewGroup) :
    AbstractMainCardViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.container_main_interactive_radar_card, parent, false)
    ) {

    private val composeView: ComposeView = itemView.findViewById(R.id.compose_view)

    init {
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    }

    override fun onBindView(
        activity: BreezyActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean
    ) {
        super.onBindView(activity, location, provider, listAnimationEnabled, itemAnimationEnabled)

        composeView.setContent {
            // Use app theme if available, or MaterialTheme defaults
            // BreezyTheme might be the theme wrapper
             InteractiveRadarCard(location = location)
        }
    }
}
