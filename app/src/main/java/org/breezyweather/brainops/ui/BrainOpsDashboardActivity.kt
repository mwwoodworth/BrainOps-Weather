package org.breezyweather.brainops.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import org.breezyweather.brainops.ui.BrainOpsSettingsActivity
import org.breezyweather.brainops.BrainOpsConfigStore
import org.breezyweather.brainops.FeatureFlags

class BrainOpsDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = BrainOpsConfigStore(this).loadConfig()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0f172a)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            SectionTitle(text = "Weather Overview")
                        }
                        item { GlassCard(title = "Current Conditions", body = "Placeholder for current weather.") }
                        item { GlassCard(title = "Hourly Forecast", body = "Placeholder for next 6 hours.") }
                        item { GlassCard(title = "Daily Forecast", body = "Placeholder for next 5 days.") }
                        item { GlassCard(title = "Weather Alerts", body = "No active alerts.") }

                        if (FeatureFlags.isBrainOpsEnabled(config)) {
                            item { SectionTitle(text = "Ops Impact") }
                            item {
                                GlassCard(
                                    title = "Operations Impact",
                                    body = "3 crews available, 2 jobs at risk.\nTap settings to enable live data."
                                )
                            }
                        } else {
                            item {
                                val context = LocalContext.current
                                Button(onClick = {
                                    context.startActivity(
                                        Intent(context, BrainOpsSettingsActivity::class.java)
                                    )
                                }) {
                                    Text("Enable BrainOps", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFFe2e8f0),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun GlassCard(
    title: String,
    body: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x991f2937)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = CardDefaults.outlinedCardBorder().brush
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(
                text = body,
                color = Color(0xFF94a3b8),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
