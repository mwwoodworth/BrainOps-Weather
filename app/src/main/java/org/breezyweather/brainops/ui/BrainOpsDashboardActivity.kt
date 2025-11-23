package org.breezyweather.brainops.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.breezyweather.brainops.BrainOpsConfig
import org.breezyweather.brainops.BrainOpsConfigStore
import org.breezyweather.brainops.FeatureFlags
import org.breezyweather.brainops.auth.AuthRepository

class BrainOpsDashboardActivity : ComponentActivity() {

    private val configStore by lazy { BrainOpsConfigStore(this) }
    private val authRepo by lazy { AuthRepository(this) }
    private var cachedConfig: BrainOpsConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cachedConfig = configStore.loadConfig()
        val config = cachedConfig ?: BrainOpsConfig()

        setContent {
            BrainOpsDashboardScreen(
                config = config,
                authRepo = authRepo
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val latestConfig = configStore.loadConfig()
        if (cachedConfig != latestConfig) {
            cachedConfig = latestConfig
            recreate()
        }
    }
}

@Composable
private fun BrainOpsDashboardScreen(
    config: BrainOpsConfig,
    authRepo: AuthRepository
) {
    val vm: BrainOpsViewModel = viewModel(
        factory = BrainOpsViewModel.Factory(config, authRepo)
    )
    val opsImpact by vm.opsImpact.collectAsState()
    val scope = rememberCoroutineScope()
    val userEmail = remember { mutableStateOf(authRepo.currentUserEmail()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(config) {
        if (FeatureFlags.isBrainOpsEnabled(config)) {
            vm.loadOpsImpact()
        }
    }

    DisposableEffect(lifecycleOwner, config) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                userEmail.value = authRepo.currentUserEmail()
                if (FeatureFlags.isBrainOpsEnabled(config)) {
                    vm.loadOpsImpact()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                item {
                    GlassCard(
                        title = "BrainOps Account",
                        body = userEmail.value?.let { "Signed in as $it" }
                            ?: "Not signed in. Use BrainOps Login to authenticate and pull live data."
                    ) {
                        val context = LocalContext.current
                        Button(onClick = {
                            context.startActivity(Intent(context, BrainOpsLoginActivity::class.java))
                        }) {
                            Text("BrainOps Login", color = Color.White)
                        }
                        Button(
                            modifier = Modifier.padding(top = 8.dp),
                            onClick = {
                                scope.launch {
                                    authRepo.signOut()
                                    userEmail.value = null
                                }
                            }
                        ) {
                            Text("Sign Out", color = Color.White)
                        }
                    }
                }
                item { GlassCard(title = "Current Conditions", body = "Loaded from weather API when enabled.") }
                item { GlassCard(title = "Hourly Forecast", body = "Loaded from weather API when enabled.") }
                item { GlassCard(title = "Daily Forecast", body = "Loaded from weather API when enabled.") }
                item { GlassCard(title = "Weather Alerts", body = "Uses ERP alerts endpoint.") }

                if (FeatureFlags.isBrainOpsEnabled(config)) {
                    item { SectionTitle(text = "Ops Impact") }
                    item {
                        when (opsImpact) {
                            is UiState.Loading -> GlassCard(title = "Operations Impact", body = "Loading...") {
                                CircularProgressIndicator(color = Color.White)
                            }
                            is UiState.Error -> GlassCard(
                                title = "Operations Impact",
                                body = (opsImpact as UiState.Error).message
                            )
                            is UiState.Success -> {
                                val data = (opsImpact as UiState.Success).data
                                GlassCard(
                                    title = "Operations Impact",
                                    body = """
                                                Weather: ${data.weatherStatus} | Safe: ${data.safeToWork} (score ${data.workScore})
                                                Active tasks: ${data.activeTasks} | Agents: ${data.agentCount} | Risk: ${data.riskLevel}
                                                Alerts: ${data.workAlerts.joinToString().ifEmpty { "None" }}
                                            """.trimIndent()
                                )
                            }
                        }
                    }
                    item { RadarCard(config = config) }
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
    trailing: (@Composable () -> Unit)? = null,
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
            if (trailing != null) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    trailing()
                }
            }
        }
    }
}

@Composable
private fun RadarCard(
    config: BrainOpsConfig
) {
    GlassCard(
        title = "Live Radar",
        body = "Experimental radar view for the primary region."
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp)),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(config.effectiveRadarUrl)
                }
            }
        )
    }
}
