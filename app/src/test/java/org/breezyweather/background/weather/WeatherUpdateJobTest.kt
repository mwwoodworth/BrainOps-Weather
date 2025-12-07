package org.breezyweather.background.weather

import breezyweather.domain.location.model.Location
import breezyweather.domain.weather.model.Base
import breezyweather.domain.weather.model.Weather
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.Date
import java.util.TimeZone

class WeatherUpdateJobTest {

    private lateinit var originalTimeZone: TimeZone

    @BeforeEach
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @AfterEach
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun `uses location timezone when deciding to refresh secondary locations`() {
        val location = Location(
            timeZone = TimeZone.getTimeZone("Asia/Tokyo"),
            weather = Weather(base = Base(refreshTime = Date.from(Instant.parse("2024-03-09T00:00:00Z"))))
        )

        val now = Date.from(Instant.parse("2024-03-09T15:30:00Z"))

        assertTrue(shouldRefreshSecondaryLocation(location, now))
    }

    @Test
    fun `does not refresh when location date matches last refresh`() {
        val location = Location(
            timeZone = TimeZone.getTimeZone("Asia/Tokyo"),
            weather = Weather(base = Base(refreshTime = Date.from(Instant.parse("2024-03-09T00:00:00Z"))))
        )

        val now = Date.from(Instant.parse("2024-03-09T11:00:00Z"))

        assertFalse(shouldRefreshSecondaryLocation(location, now))
    }
}
