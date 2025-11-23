package org.breezyweather.brainops.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.breezyweather.brainops.BrainOpsConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Simple stubs to keep the first increment non-blocking
@Serializable
data class AlertsResponse(val alerts: List<String> = emptyList())
@Serializable
data class TasksResponse(val tasks: List<String> = emptyList())
@Serializable
data class ImpactRequest(val tenantId: String, val locations: List<String> = emptyList())
@Serializable
data class ImpactResponse(val summary: String = "3 crews available, 2 jobs at risk")

interface BrainOpsApi {
    @GET("weather/alerts")
    suspend fun getAlerts(
        @Header("Authorization") auth: String,
        @Header("X-Tenant-ID") tenantIdHeader: String,
        @Query("tenant_id") tenantId: String
    ): Response<AlertsResponse>

    @GET("tasks")
    suspend fun getTasks(
        @Header("Authorization") auth: String,
        @Header("X-Tenant-ID") tenantIdHeader: String,
        @Query("tenant_id") tenantId: String,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<TasksResponse>
}

class BrainOpsApiClient(
    private val config: BrainOpsConfig,
    private val authTokenProvider: () -> String?
) {
    private val headersInterceptor = Interceptor { chain ->
        val token = authTokenProvider()
        val authHeader = token?.let { "Bearer $it" }
            ?: if (BuildConfig.DEBUG) "Bearer ${config.devApiKey}" else null
        if (authHeader == null) {
            throw IllegalStateException("Missing auth token")
        }
        val request = chain.request().newBuilder()
            .addHeader("Authorization", authHeader)
            .addHeader("X-Tenant-ID", config.tenantId.ifBlank { BuildConfig.DEFAULT_TENANT_ID })
            .build()
        chain.proceed(request)
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(headersInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(config.apiBaseUrl)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .client(okHttp)
        .build()

    val api: BrainOpsApi = retrofit.create(BrainOpsApi::class.java)
}
