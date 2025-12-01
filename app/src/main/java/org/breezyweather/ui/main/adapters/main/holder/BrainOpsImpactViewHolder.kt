package org.breezyweather.ui.main.adapters.main.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberUpdatedState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import breezyweather.domain.location.model.Location
import org.breezyweather.R
import org.breezyweather.brainops.BrainOpsConfigStore
import org.breezyweather.brainops.FeatureFlags
import org.breezyweather.common.activities.BreezyActivity
import org.breezyweather.ui.theme.resource.providers.ResourceProvider

/**
 * Lightweight BrainOps impact card to surface integration status and actions.
 * Data fetches stay out of the hot path; we only reflect config and deep links.
 */
class BrainOpsImpactViewHolder(parent: ViewGroup) : AbstractMainCardViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.container_main_brainops_impact_card, parent, false)
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

        val config = BrainOpsConfigStore(itemView.context).loadConfig()
        composeView.setContent {
            org.breezyweather.ui.theme.compose.BreezyWeatherTheme {
                BrainOpsImpactCard(
                    enabled = FeatureFlags.isBrainOpsEnabled(config) && config.apiKey.isNotBlank(),
                    tenantId = config.effectiveTenantId.ifBlank { "not-set" },
                    hasRadar = config.radarUrl.isNotBlank(),
                    onOpenDashboard = {
                        activity.startActivity(
                            android.content.Intent(activity, org.breezyweather.brainops.ui.BrainOpsDashboardActivity::class.java)
                        )
                    },
                    onOpenSettings = {
                        activity.startActivity(
                            android.content.Intent(activity, org.breezyweather.brainops.ui.BrainOpsSettingsActivity::class.java)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun BrainOpsImpactCard(
    enabled: Boolean,
    tenantId: String,
    hasRadar: Boolean,
    onOpenDashboard: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val accent = if (enabled) Color(0xFF22c55e) else Color(0xFFf59e0b)
    val accentMuted = if (enabled) Color(0x3322c55e) else Color(0x33f59e0b)
    val dashboardAction by rememberUpdatedState(onOpenDashboard)
    val settingsAction by rememberUpdatedState(onOpenSettings)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF0f172a),
                        Color(0xFF0b1220)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.brainops_card_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = if (enabled) {
                        stringResource(
                            id = R.string.brainops_card_status_connected,
                            tenantId
                        )
                    } else {
                        stringResource(id = R.string.brainops_card_status_disconnected)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFcbd5e1)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusDot(color = accent)
                Text(
                    text = if (enabled) stringResource(id = R.string.brainops_card_live) else stringResource(id = R.string.brainops_card_offline),
                    style = MaterialTheme.typography.labelMedium,
                    color = accent
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(accentMuted)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(id = R.string.brainops_card_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = stringResource(
                        id = if (hasRadar) R.string.brainops_card_radar_ready else R.string.brainops_card_radar_missing
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFe2e8f0)
                )
            }
            Button(onClick = dashboardAction) {
                Text(text = stringResource(id = R.string.brainops_card_open_dashboard))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = settingsAction,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.brainops_card_edit_settings))
            }
            TextButton(
                onClick = dashboardAction,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.brainops_card_view_jobs),
                    color = Color(0xFF38bdf8)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun StatusDot(color: Color) {
    Spacer(
        modifier = Modifier
            .size(12.dp)
            .clip(RoundedCornerShape(50))
            .background(color)
    )
}
