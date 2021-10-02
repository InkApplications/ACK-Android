package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.ParrotStringResources
import com.inkapplications.aprs.android.symbol.SymbolFactoryStub
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.aprs.data.PacketSource
import com.inkapplications.karps.structures.*
import com.inkapplications.karps.structures.unit.*
import inkapplications.spondee.measure.Fahrenheit
import inkapplications.spondee.measure.MilesPerHour
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StationViewModelFactoryTest {
    @Test
    fun unknownPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)
        val packet = CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Unknown(
                received = Instant.fromEpochMilliseconds(0),
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                body = "test",
                raw = "",
            ),
            source = PacketSource.AprsIs,
        )
        val result = factory.create(packet, false, false)

        assertFalse(result.mapVisible, "Map is hidden for unknown packet")
        assertFalse(result.temperatureVisible, "Temperature is hidden for non weather packet")
        assertFalse(result.windVisible, "Wind is hidden for non weather packet")
        assertEquals("KE0YOG-1", result.name)
        assertTrue(result.debugDataVisible, "Debug Data visible for unknown packets")
    }

    @Test
    fun positionlessWeatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Weather(
                received = Instant.fromEpochMilliseconds(0),
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                temperature = Fahrenheit.of(72),
                precipitation = Precipitation(),
                windData = WindData(Degrees.of(12), MilesPerHour.of(34), MilesPerHour.of(56)),
                coordinates = null,
                humidity = null,
                irradiance = null,
                timestamp = null,
                symbol = null,
                pressure = null,
                raw = "",
            ),
            source = PacketSource.AprsIs,
        )
        val result = factory.create(packet, false, false)

        assertFalse(result.mapVisible, "Map is hidden for positionless weather")
        assertTrue(result.temperatureVisible, "Temperature is visible when value is specified")
        assertTrue(result.windVisible, "Wind is visible when value is specified")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34.0mph|56.0mph", result.wind)
        assertEquals("KE0YOG-1", result.name)
        assertFalse(result.debugDataVisible)
    }

    @Test
    fun weatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Weather(
                received = Instant.fromEpochMilliseconds(0L),
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                coordinates = GeoCoordinates(1.0.latitude, 2.0.longitude),
                temperature = Fahrenheit.of(72),
                precipitation = Precipitation(),
                windData = WindData(Degrees.of(12), MilesPerHour.of(34), MilesPerHour.of(56)),
                humidity = null,
                irradiance = null,
                timestamp = null,
                symbol = null,
                pressure = null,
                raw = "",
            ),
            source = PacketSource.AprsIs,
        )
        val result = factory.create(packet, false, false)

        assertTrue(result.mapVisible, "Map is visible for positioned weather packet")
        assertEquals(1, result.markers.size)
        assertTrue(result.temperatureVisible, "Temperature is visible when value is specified")
        assertTrue(result.windVisible, "Wind is visible when value is specified")
        assertFalse(result.altitudeVisible, "Altitude visible when value is unspecified")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34.0mph|56.0mph", result.wind)
        assertEquals("KE0YOG-1", result.name)
        assertFalse(result.debugDataVisible)
    }

    @Test
    fun emptyWeatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Weather(
                received = Instant.fromEpochMilliseconds(0),
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                coordinates = GeoCoordinates(1.latitude, 2.longitude),
                precipitation = Precipitation(),
                windData = WindData(null, null, null),
                temperature = null,
                humidity = null,
                irradiance = null,
                timestamp = null,
                symbol = null,
                pressure = null,
                raw = "",
            ),
            source = PacketSource.AprsIs,
        )
        val result = factory.create(packet, false, false)

        assertTrue(result.mapVisible, "Map is shown for positioned weather packet")
        assertEquals(1, result.markers.size)
        assertFalse(result.temperatureVisible, "Temperature is hidden when value is null")
        assertFalse(result.windVisible, "Wind is hidden when value is null")
        assertFalse(result.altitudeVisible, "Altitude is hidden when value is null")
        assertEquals("KE0YOG-1", result.name)
        assertFalse(result.debugDataVisible)
    }

    @Test
    fun positionPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = CapturedPacket(
            id = 1,
            received = 2,
            data = AprsPacket.Position(
                received = Instant.fromEpochMilliseconds(0),
                dataTypeIdentifier = '!',
                source = Address("KE0YOG", "1"),
                destination = Address("KE0YOG", "2"),
                digipeaters = emptyList(),
                coordinates = GeoCoordinates(1.latitude, 2.longitude),
                symbol = symbolOf('/', 'a'),
                comment = "test",
                altitude = null,
                timestamp = null,
                trajectory = null,
                range = null,
                transmitterInfo = null,
                signalInfo = null,
                directionReportExtra = null,
                raw = "",
            ),
            source = PacketSource.AprsIs,
        )
        val result = factory.create(packet, false, false)

        assertTrue(result.mapVisible, "Map is visible for position packet")
        assertFalse(result.temperatureVisible, "Temperature is hidden for non weather packet")
        assertFalse(result.windVisible, "Wind is hidden for non weather packet")
        assertEquals(1, result.markers.size)
        assertEquals("KE0YOG-1", result.name)
        assertEquals("test", result.comment)
        assertFalse(result.debugDataVisible)
    }
}
