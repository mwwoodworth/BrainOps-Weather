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
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Serializable
data class Agent(
    val id: String = "",
    val name: String = "",
    val category: String? = null,
    val enabled: Boolean = true
)

@Serializable
data class AgentsResponse(
    val agents: List<Agent> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val page_size: Int = 0
)

interface AgentsApi {
    @GET("agents")
    suspend fun getAgents(
        @Header("Authorization") auth: String,
        @Query("category") category: String? = null,
        @Query("enabled") enabled: Boolean = true
    ): Response<AgentsResponse>
}

class AgentsApiClient(
    private val config: BrainOpsConfig,
    private val authTokenProvider: () -> String?
) {
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .addInterceptor { chain ->
            val token = authTokenProvider()
            val authHeader = token?.let { "Bearer $it" }
                ?: "Bearer ${config.devApiKey}"
            val req = chain.request().newBuilder()
                .addHeader("Authorization", authHeader)
                .build()
            chain.proceed(req)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(config.aiBaseUrl)
        .addConverterFactory(
            Json { ignoreUnknownKeys = true }
                .asConverterFactory("application/json".toMediaType())
        )
        .client(okHttp)
        .build()

    val api: AgentsApi = retrofit.create(AgentsApi::class.java)
}
