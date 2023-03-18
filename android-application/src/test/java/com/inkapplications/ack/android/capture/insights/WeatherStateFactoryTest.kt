package com.inkapplications.ack.android.capture.insights

import com.inkapplications.ack.android.DummyPacket
import com.inkapplications.ack.android.EpochFormatterFake
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.Precipitation
import com.inkapplications.ack.structures.WindData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.Ssid
import com.inkapplications.ack.structures.station.StationAddress
import inkapplications.spondee.measure.metric.celsius
import inkapplications.spondee.measure.us.fahrenheit
import inkapplications.spondee.measure.us.inches
import inkapplications.spondee.measure.us.milesPerHour
import inkapplications.spondee.scalar.percent
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherStateFactoryTest {
    private val factory = WeatherStateFactory(EpochFormatterFake)

    @Test
    fun weatherTemperature() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                route = DummyPacket.parsed.route.copy(
                    source = StationAddress(Callsign("KE0YOG"), Ssid("9"))
                ),
                data = PacketData.Weather(
                    temperature = 80.fahrenheit,
                )
            ),
            received = Instant.fromEpochMilliseconds(123),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals("80ºF", result.temperature)
        assertEquals(InsightsWeatherState.WeatherIcon.Normal, result.icon)
        assertEquals("KE0YOG-9", result.weatherReporter)
        assertEquals("123", result.weatherReportTime)
    }

    @Test
    fun noWeather() {
        val result = factory.createState(
            weatherPacket = null,
            metric = false,
        )

        assertTrue(result is InsightsWeatherState.Unknown)
    }

    @Test
    fun weatherNoTemp() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather()
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )

        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals("--", result.temperature)
    }

    @Test
    fun weatherSnowy() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather(
                    precipitation = Precipitation(
                        snowLast24Hours = 12.inches,
                    )
                )
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals(InsightsWeatherState.WeatherIcon.Snow, result.icon)
    }

    @Test
    fun weatherRainy() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather(
                    precipitation = Precipitation(
                        rainLastHour = 12.inches,
                    )
                )
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals(InsightsWeatherState.WeatherIcon.Rain, result.icon)
    }

    @Test
    fun weatherWindy() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather(
                    windData = WindData(
                        speed = 25.milesPerHour,
                    )
                )
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals(InsightsWeatherState.WeatherIcon.Windy, result.icon)
    }

    @Test
    fun weatherGusty() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather(
                    windData = WindData(
                        gust = 25.milesPerHour,
                    )
                )
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals(InsightsWeatherState.WeatherIcon.Windy, result.icon)
    }

    @Test
    fun weatherHumid() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather(
                    humidity = 80.percent,
                    temperature = 80.fahrenheit,
                )
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = false,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals(InsightsWeatherState.WeatherIcon.Humid, result.icon)
    }

    @Test
    fun metricFormat() {
        val fakePacket = DummyPacket.copy(
            parsed = DummyPacket.parsed.copy(
                data = PacketData.Weather(
                    temperature = 25.celsius,
                )
            ),
        )
        val result = factory.createState(
            weatherPacket = fakePacket,
            metric = true,
        )
        assertTrue(result is InsightsWeatherState.DisplayRecent)
        assertEquals("25.0ºC", result.temperature)
    }
}
