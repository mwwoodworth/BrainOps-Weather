package org.breezyweather.brainops.auth

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.auth

class AuthRepository(context: Context) {
    private val supabase = SupabaseClient.client
    private val storage = SecureStorage(context)

    suspend fun signInWithEmail(email: String, password: String): Result<UserSession> {
        return runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val session = supabase.auth.currentSessionOrNull()
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
        SupabaseClient.handleDeepLink(uri)
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
