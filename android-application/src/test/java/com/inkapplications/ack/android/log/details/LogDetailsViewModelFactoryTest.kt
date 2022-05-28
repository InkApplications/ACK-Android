package com.inkapplications.ack.android.log.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.WindData
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.symbolOf
import inkapplications.spondee.measure.Fahrenheit
import inkapplications.spondee.measure.MilesPerHour
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import org.junit.Test
import kotlin.test.*

class StationViewModelFactoryTest {
    val dummySummaryFactory = SummaryFactory(ParrotStringResources)

    @Test
    fun nameFormatting() {
        val factory = LogDetailsViewModelFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Unknown(body = "Test").toTestPacket().copy(
            route = testRoute.copy(source = StationAddress("TEST", "4"))
        ).toTestCapturedPacket()

        val result = factory.create(LogDetailData(packet = packet))

        assertEquals("TEST-4", result.name)
    }

    @Test
    fun icons() {
        val factory = LogDetailsViewModelFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val base = PacketData.Unknown(
            body = "Test"
        ).toTestPacket().toTestCapturedPacket()

        val localResult = LogDetailData(packet = base.copy(source = PacketSource.Local)).let(factory::create)
        val isResult = LogDetailData(packet = base.copy(source = PacketSource.AprsIs)).let(factory::create)
        val ax25Result = LogDetailData(packet = base.copy(source = PacketSource.Ax25)).let(factory::create)

        assertEquals(Icons.Default.Storage, localResult.receiveIcon)
        assertEquals(Icons.Default.Cloud, isResult.receiveIcon)
        assertEquals(Icons.Default.SettingsInputAntenna, ax25Result.receiveIcon)
    }

    @Test
    fun debugData() {
        val factory = LogDetailsViewModelFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Unknown(
            body = "Test"
        ).toTestPacket().toTestCapturedPacket().copy(
            raw = "Test Debug".toByteArray()
        )
        val data = LogDetailData(
            packet = packet,
            debug = true
        )

        val result = factory.create(data)

        assertEquals("Test Debug", result.rawSource)
    }

    @Test
    fun unknownPacket() {
        val factory = LogDetailsViewModelFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Unknown(body = "Test").toTestPacket().copy(
            route = testRoute.copy(source = StationAddress("TEST", "4"))
        ).toTestCapturedPacket()

        val result = factory.create(LogDetailData(packet = packet))

        assertTrue(result.markers.isEmpty(), "No map markers for unknown packet")
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertNull(result.comment, "No comment for unknown packet")
        assertNull(result.altitude, "No altitude for unknown packet")
        assertNull(result.telemetryValues, "No telemetry for unknown packet")
        assertNull(result.telemetrySequence, "No telemetry for unknown packet")
        assertNull(result.rawSource, "Debug data hidden")
    }

    @Test
    fun positionlessWeatherPacket() {
        val factory = LogDetailsViewModelFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Weather(
            temperature = Fahrenheit.of(72),
            windData = WindData(
                direction = Degrees.of(12),
                speed = MilesPerHour.of(34),
                gust = MilesPerHour.of(56),
            )
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factory.create(data)

        assertTrue(result.markers.isEmpty(), "No map markers for positionless weather")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertNull(result.comment, "No comment for positionless weather")
        assertNull(result.altitude, "No altitude for positionless weather")
        assertNull(result.telemetryValues, "No telemetry for positionless weather")
        assertNull(result.telemetrySequence, "No telemetry for positionless weather")
        assertNull(result.rawSource, "Debug data hidden")
    }

    @Test
    fun weatherPacket() {
        val factory = LogDetailsViewModelFactory(DummyMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.0.latitude, 2.0.longitude),
            temperature = Fahrenheit.of(72),
            windData = WindData(Degrees.of(12), MilesPerHour.of(34), MilesPerHour.of(56)),
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factory.create(data)

        assertEquals(1, result.markers.size)
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertNull(result.comment, "No comment for weather packet")
        assertNull(result.altitude, "No altitude for weather packet")
        assertNull(result.telemetryValues, "No telemetry for weather packet")
        assertNull(result.telemetrySequence, "No telemetry for weather packet")
        assertNull(result.rawSource, "Debug data hidden")
    }

    @Test
    fun emptyWeatherPacket() {
        val factory = LogDetailsViewModelFactory(DummyMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factory.create(data)

        assertEquals(1, result.markers.size)
        assertNull(result.temperature, "No temperature for empty weather packet")
        assertNull(result.wind, "No wind data for empty non weather packet")
        assertNull(result.comment, "No comment for unknown packet")
        assertNull(result.altitude, "No altitude for unknown packet")
        assertNull(result.telemetryValues, "No telemetry for unknown packet")
        assertNull(result.telemetrySequence, "No telemetry for unknown packet")
        assertNull(result.rawSource, "Debug data hidden")
    }

    @Test
    fun positionPacket() {
        val factory = LogDetailsViewModelFactory(DummyMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Position(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
            symbol = symbolOf('/', 'a'),
            comment = "test",
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factory.create(data)

        assertEquals(1, result.markers.size)
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertEquals("test", result.comment)
        assertNull(result.altitude, "No altitude for position packet")
        assertNull(result.telemetryValues, "No telemetry for position packet")
        assertNull(result.telemetrySequence, "No telemetry for position packet")
        assertNull(result.rawSource, "Debug data hidden")
    }
}
