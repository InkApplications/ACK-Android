package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.ParrotStringResources
import com.inkapplications.aprs.android.symbol.SymbolFactoryStub
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StationViewModelFactoryTest {
    @Test
    fun unknownPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val result = factory.create(CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Unknown(
                received = 0L.asTimestamp,
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                body = "test"
            )
        ))

        assertFalse(result.mapVisible, "Map is hidden for unknown packet")
        assertFalse(result.temperatureVisible, "Temperature is hidden for non weather packet")
        assertFalse(result.windVisible, "Wind is hidden for non weather packet")
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun positionlessWeatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val result = factory.create(CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Weather(
                received = 0L.asTimestamp,
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                body = "test",
                temperature = 72.degreesFahrenheit,
                precipitation = Precipitation(),
                windData = WindData(12.degreesBearing, 34.mph, 56.mph)
            )
        ))

        assertFalse(result.mapVisible, "Map is hidden for positionless weather")
        assertTrue(result.temperatureVisible, "Temperature is visible when value is specified")
        assertTrue(result.windVisible, "Wind is visible when value is specified")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun weatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val result = factory.create(CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Weather(
                received = 0L.asTimestamp,
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                body = "test",
                position = Coordinates(Latitude(1.0), Longitude(2.0)),
                temperature = 72.degreesFahrenheit,
                precipitation = Precipitation(),
                windData = WindData(12.degreesBearing, 34.mph, 56.mph)
            )
        ))

        assertTrue(result.mapVisible, "Map is visible for positioned weather packet")
        assertEquals(1, result.markers.size)
        assertTrue(result.temperatureVisible, "Temperature is visible when value is specified")
        assertTrue(result.windVisible, "Wind is visible when value is specified")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun emptyWeatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val result = factory.create(CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Weather(
                received = 0L.asTimestamp,
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                body = "test",
                position = Coordinates(Latitude(1.0), Longitude(2.0)),
                precipitation = Precipitation(),
                windData = WindData(null, null, null)
            )
        ))

        assertTrue(result.mapVisible, "Map is shown for positioned weather packet")
        assertEquals(1, result.markers.size)
        assertFalse(result.temperatureVisible, "Temperature is hidden when value is null")
        assertFalse(result.windVisible, "Wind is hidden when value is null")
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun positionPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val result = factory.create(CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Position(
                received = 0L.asTimestamp,
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                body = "test",
                coordinates = Coordinates(Latitude(1.0), Longitude(2.0)),
                symbol = symbolOf('/', 'a')
            )
        ))

        assertTrue(result.mapVisible, "Map is visible for position packet")
        assertFalse(result.temperatureVisible, "Temperature is hidden for non weather packet")
        assertFalse(result.windVisible, "Wind is hidden for non weather packet")
        assertEquals(1, result.markers.size)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }
}
