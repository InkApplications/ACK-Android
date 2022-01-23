package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.symbol.SymbolFactoryStub
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.karps.structures.*
import inkapplications.spondee.measure.Fahrenheit
import inkapplications.spondee.measure.MilesPerHour
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
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
            parsed = AprsPacket(
                route = testRoute.copy(
                    source = Address("KE0YOG", "1"),
                ),
                data = PacketData.Unknown(
                    body = "test",
                ),
            ),
            source = PacketSource.AprsIs,
            raw = byteArrayOf(),
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

        val packet = PacketData.Weather(
            temperature = Fahrenheit.of(72),
            windData = WindData(
                direction = Degrees.of(12),
                speed = MilesPerHour.of(34),
                gust = MilesPerHour.of(56),
            )
        ).toTestPacket().toTestCapturedPacket()
        val result = factory.create(packet, false, false)

        assertFalse(result.mapVisible, "Map is hidden for positionless weather")
        assertTrue(result.temperatureVisible, "Temperature is visible when value is specified")
        assertTrue(result.windVisible, "Wind is visible when value is specified")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34.0mph|56.0mph", result.wind)
        assertEquals("KE0YOG", result.name)
        assertFalse(result.debugDataVisible)
    }

    @Test
    fun weatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.0.latitude, 2.0.longitude),
            temperature = Fahrenheit.of(72),
            windData = WindData(Degrees.of(12), MilesPerHour.of(34), MilesPerHour.of(56)),
        ).toTestPacket().toTestCapturedPacket()
        val result = factory.create(packet, false, false)

        assertTrue(result.mapVisible, "Map is visible for positioned weather packet")
        assertEquals(1, result.markers.size)
        assertTrue(result.temperatureVisible, "Temperature is visible when value is specified")
        assertTrue(result.windVisible, "Wind is visible when value is specified")
        assertFalse(result.altitudeVisible, "Altitude visible when value is unspecified")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34.0mph|56.0mph", result.wind)
        assertEquals("KE0YOG", result.name)
        assertFalse(result.debugDataVisible)
    }

    @Test
    fun emptyWeatherPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
        ).toTestPacket().toTestCapturedPacket()
        val result = factory.create(packet, false, false)

        assertTrue(result.mapVisible, "Map is shown for positioned weather packet")
        assertEquals(1, result.markers.size)
        assertFalse(result.temperatureVisible, "Temperature is hidden when value is null")
        assertFalse(result.windVisible, "Wind is hidden when value is null")
        assertFalse(result.altitudeVisible, "Altitude is hidden when value is null")
        assertEquals("KE0YOG", result.name)
        assertFalse(result.debugDataVisible)
    }

    @Test
    fun positionPacket() {
        val factory = StationViewModelFactory(SymbolFactoryStub, ParrotStringResources)

        val packet = PacketData.Position(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
            symbol = symbolOf('/', 'a'),
            comment = "test",
        ).toTestPacket().toTestCapturedPacket()
        val result = factory.create(packet, false, false)

        assertTrue(result.mapVisible, "Map is visible for position packet")
        assertFalse(result.temperatureVisible, "Temperature is hidden for non weather packet")
        assertFalse(result.windVisible, "Wind is hidden for non weather packet")
        assertEquals(1, result.markers.size)
        assertEquals("KE0YOG", result.name)
        assertEquals("test", result.comment)
        assertFalse(result.debugDataVisible)
    }
}
