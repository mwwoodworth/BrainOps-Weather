package org.breezyweather.brainops.auth

import android.content.Intent
import android.net.Uri
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    private const val SUPABASE_URL = "https://yomagoqdmxszqtdwuhab.supabase.co"
    private const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlvbWFnb3FkbXhzenF0ZHd1aGFiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMzNzY3NzcsImV4cCI6MjA0ODk1Mjc3N30.5tL0ms5Bs9PqQs_RdkBj8Xq_QhqoUvMKZTML3MCXbFw"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            scheme = "com.brainops.weather"
            host = "auth-callback"
            flowType = FlowType.PKCE
        }
        install(Postgrest)
    }

    suspend fun handleDeepLink(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        client.handleDeeplinks(intent)
    }
}
