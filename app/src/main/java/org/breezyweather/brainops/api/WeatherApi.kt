package org.breezyweather.brainops.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
data class WeatherMain(
    val temp: Double? = null,
    val feels_like: Double? = null,
    val temp_min: Double? = null,
    val temp_max: Double? = null,
    val pressure: Double? = null,
    val humidity: Double? = null
)

@Serializable
data class WeatherCondition(
    val main: String = "",
    val description: String = "",
    val icon: String? = null
)

@Serializable
data class WeatherData(
    val name: String = "",
    val main: WeatherMain = WeatherMain(),
    val weather: List<WeatherCondition> = emptyList(),
    val dt: Long? = null
)

@Serializable
data class WorkSafety(
    val safeToWork: Boolean = true,
    val score: Int = 100,
    val alerts: List<String> = emptyList(),
    val recommendations: List<String> = emptyList()
)

@Serializable
data class WeatherResponse(
    val weather: WeatherData? = null,
    val workSafety: WorkSafety? = null,
    val timestamp: String? = null
)

@Serializable
data class ForecastItem(
    val dt: Long? = null,
    val main: WeatherMain? = null,
    val weather: List<WeatherCondition>? = null,
    val pop: Double? = null
)

@Serializable
data class ForecastList(
    val list: List<ForecastItem> = emptyList()
)

@Serializable
data class ForecastResponse(
    val forecast: ForecastList = ForecastList(),
    val timestamp: String? = null
)

interface WeatherApi {
    @GET("weather/current")
    suspend fun getCurrentWeather(
        @Header("Authorization") auth: String,
        @Header("X-Tenant-ID") tenantIdHeader: String,
        @Query("lat") lat: Double = 38.8339,
        @Query("lon") lon: Double = -104.8214,
        @Query("analyze") analyze: Boolean = true
    ): Response<WeatherResponse>

    @GET("weather/forecast")
    suspend fun getForecast(
        @Header("Authorization") auth: String,
        @Header("X-Tenant-ID") tenantIdHeader: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("hours") hours: Int = 24
    ): Response<ForecastResponse>
}

class WeatherApiClient(
    private val config: BrainOpsConfig,
    private val authTokenProvider: () -> String?
) {
    private fun resolveAuthHeader(): String? {
        val token = authTokenProvider()
        return token?.let { "Bearer $it" }
            ?: config.apiKey.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
            ?: config.devApiKey.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(config.weatherBaseUrl)
        .addConverterFactory(
            Json { ignoreUnknownKeys = true }
                .asConverterFactory("application/json".toMediaType())
        )
        .client(
            okHttp.newBuilder().addInterceptor { chain ->
                val authHeader = resolveAuthHeader() ?: throw IllegalStateException("Missing auth token")
                val req = chain.request().newBuilder()
                    .header("Authorization", authHeader)
                    .header("X-Tenant-ID", config.effectiveTenantId)
                    .build()
                chain.proceed(req)
            }.build()
        )
        .build()

    val api: WeatherApi = retrofit.create(WeatherApi::class.java)
}
