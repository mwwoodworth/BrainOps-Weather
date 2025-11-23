package org.breezyweather.brainops.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.breezyweather.brainops.auth.AuthRepository

class BrainOpsLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepo = AuthRepository(this)

        setContent {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var message by remember { mutableStateOf("") }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0f172a)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("BrainOps Login", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Text("Sign in with Supabase email/password or request a magic link.", color = Color(0xFF94a3b8))

                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Email") }
                        )
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Password") }
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                lifecycleScope.launch {
                                    val result = authRepo.signInWithEmail(email.trim(), password)
                                    message = result.exceptionOrNull()?.message ?: "Signed in"
                                    if (result.isSuccess) finish()
                                }
                            }
                        ) {
                            Text("Sign In", color = Color.White)
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                lifecycleScope.launch {
                                    val result = authRepo.signInWithMagicLink(email.trim())
                                    message = result.exceptionOrNull()?.message ?: "Magic link sent"
                                }
                            }
                        ) {
                            Text("Send Magic Link", color = Color.White)
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                lifecycleScope.launch {
                                    authRepo.signOut()
                                    message = "Signed out"
                                }
                            }
                        ) {
                            Text("Sign Out", color = Color.White)
                        }

                        if (message.isNotEmpty()) {
                            Text(message, color = Color(0xFF22d3ee))
                        }
                    }
                }
            }
        }
    }
}
