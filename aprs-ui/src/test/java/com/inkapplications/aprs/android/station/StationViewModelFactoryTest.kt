package com.inkapplications.aprs.android.station

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
        val factory = StationViewModelFactory(SymbolFactoryStub)

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

        assertFalse(result.mapVisible)
        assertFalse(result.weatherVisible)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun positionlessWeatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub)

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
                windData = WindData(null, null, null)
            )
        ))

        assertFalse(result.mapVisible)
        assertTrue(result.weatherVisible)
        assertEquals("72ºF", result.weather)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun weatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub)

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
                windData = WindData(null, null, null)
            )
        ))

        assertTrue(result.mapVisible)
        assertEquals(1, result.markers.size)
        assertTrue(result.weatherVisible)
        assertEquals("72ºF", result.weather)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }

    @Test
    fun positionPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub)

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

        assertTrue(result.mapVisible)
        assertEquals(1, result.markers.size)
        assertFalse(result.weatherVisible)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
    }
}
