package org.breezyweather.brainops.auth

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.gotrue.providers.builtin.Email

class AuthRepository(context: Context) {
    private val supabase = SupabaseClient.client
    private val storage = SecureStorage(context)

    suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
        return runCatching {
            val session = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userSession = UserSession(
                accessToken = session?.accessToken,
                refreshToken = session?.refreshToken,
                userId = session?.user?.id
            )
            storage.saveSession(userSession)
            userSession
        }
    }

    suspend fun signInWithMagicLink(email: String): Result<Unit> {
        return runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
            }
        }
    }

    suspend fun handleDeepLink(uri: Uri) {
        SupabaseClient.handleDeepLink(uri.toString())
    }

    suspend fun signOut() {
        runCatching { supabase.auth.signOut() }
        storage.clear()
    }

    fun currentUserEmail(): String? {
        return supabase.auth.currentUserOrNull()?.email
    }

    fun getAccessToken(): String? {
        val liveToken = supabase.auth.currentSessionOrNull()?.accessToken
        return liveToken ?: storage.loadSession()?.accessToken
    }
}
