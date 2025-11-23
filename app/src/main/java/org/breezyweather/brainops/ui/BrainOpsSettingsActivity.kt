package org.breezyweather.brainops.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.breezyweather.brainops.BrainOpsConfig
import org.breezyweather.brainops.BrainOpsConfigStore

class BrainOpsSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val store = BrainOpsConfigStore(this)
        val initial = store.loadConfig()

        setContent {
            var apiBase by remember { mutableStateOf(initial.apiBaseUrl) }
            var apiKey by remember { mutableStateOf(initial.apiKey) }
            var tenantId by remember { mutableStateOf(initial.tenantId) }
            var radarUrl by remember { mutableStateOf(initial.radarUrl) }
            var enabled by remember { mutableStateOf(initial.enableIntegration) }
            var saved by remember { mutableStateOf(false) }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0f172a)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "BrainOps Settings",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Configure BrainOps endpoints and auth. Uses static API key for initial integration.",
                            color = Color(0xFF94a3b8),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        LabeledField(label = "API Base URL", value = apiBase) { apiBase = it }
                        LabeledField(
                            label = "API Key",
                            value = apiKey,
                            visualTransformation = PasswordVisualTransformation()
                        ) { apiKey = it }
                        LabeledField(label = "Tenant ID", value = tenantId) { tenantId = it }
                        LabeledField(label = "Radar URL (optional)", value = radarUrl) { radarUrl = it }

                        RowWithCheckbox(
                            label = "Enable BrainOps integration",
                            checked = enabled
                        ) { enabled = it }

                        Button(
                            onClick = {
                                val newConfig = BrainOpsConfig(
                                    apiBaseUrl = apiBase.trim(),
                                    apiKey = apiKey.trim(),
                                    tenantId = tenantId.trim(),
                                    enableIntegration = enabled,
                                    radarUrl = radarUrl.trim()
                                )
                                store.saveConfig(newConfig)
                                saved = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save", color = Color.White)
                        }

                        if (saved) {
                            Text(
                                text = "Saved.",
                                color = Color(0xFF22d3ee),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = Color(0xFFe2e8f0), style = MaterialTheme.typography.labelLarge)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            visualTransformation = visualTransformation
        )
    }
}

@Composable
private fun RowWithCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFFe2e8f0))
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
