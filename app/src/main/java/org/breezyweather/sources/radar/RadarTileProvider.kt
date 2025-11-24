package org.breezyweather.sources.radar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.breezyweather.BuildConfig
import org.breezyweather.ui.radar.RadarLayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadarTileProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val cache: RadarTileCache
) {
    suspend fun getTile(
        layer: RadarLayer,
        z: Int,
        x: Int,
        y: Int,
        timestamp: Long? = null
    ): Bitmap? = withContext(Dispatchers.IO) {
        // Check cache first
        cache.get(layer.layerId, z, x, y, timestamp)?.let { return@withContext it }

        // Fetch from API
        val url = buildTileUrl(layer, z, x, y, timestamp)
        val request = Request.Builder().url(url).build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.bytes()?.let { bytes ->
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also { bitmap ->
                            cache.put(layer.layerId, z, x, y, timestamp, bitmap)
                            return@withContext bitmap
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    fun buildTileUrl(
        layer: RadarLayer,
        z: Int,
        x: Int,
        y: Int,
        timestamp: Long?
    ): String {
        // Example: https://tile.openweathermap.org/map/precipitation_new/3/3/3.png?appid={API key}
        val baseUrl = "https://tile.openweathermap.org/map/${layer.endpoint}/$z/$x/$y.png"
        val apiKeyParam = "appid=${BuildConfig.OPEN_WEATHER_KEY}"
        val timeParam = if (timestamp != null) "&time=$timestamp" else ""
        
        return "$baseUrl?$apiKeyParam$timeParam"
    }
}
