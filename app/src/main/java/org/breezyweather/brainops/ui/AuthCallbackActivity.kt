package org.breezyweather.brainops.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.breezyweather.brainops.auth.AuthRepository

/**
 * Handles Supabase PKCE callback deep links.
 */
class AuthCallbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data != null) {
            val repo = AuthRepository(this)
            lifecycleScope.launch {
                repo.handleDeepLink(data)
                // After handling, return to main
                startActivity(Intent(this@AuthCallbackActivity, org.breezyweather.ui.main.MainActivity::class.java))
                finish()
            }
        } else {
            finish()
        }
    }
}
