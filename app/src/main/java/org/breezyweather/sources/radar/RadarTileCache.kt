package org.breezyweather.sources.radar

import android.graphics.Bitmap
import android.util.LruCache
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadarTileCache @Inject constructor() {
    
    // 50MB Cache
    private val memoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(50 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount
        }
    }

    fun get(layerId: String, z: Int, x: Int, y: Int, timestamp: Long?): Bitmap? {
        val key = generateKey(layerId, z, x, y, timestamp)
        return memoryCache.get(key)
    }

    fun put(layerId: String, z: Int, x: Int, y: Int, timestamp: Long?, bitmap: Bitmap) {
        val key = generateKey(layerId, z, x, y, timestamp)
        memoryCache.put(key, bitmap)
    }

    private fun generateKey(layerId: String, z: Int, x: Int, y: Int, timestamp: Long?): String {
        return "$layerId-$z-$x-$y-${timestamp ?: "latest"}"
    }
    
    fun clear() {
        memoryCache.evictAll()
    }
}
