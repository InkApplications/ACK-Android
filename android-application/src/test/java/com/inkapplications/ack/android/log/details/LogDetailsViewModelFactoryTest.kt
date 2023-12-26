package com.inkapplications.ack.android.log.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.data.PacketOrigin
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.WindData
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.symbolOf
import inkapplications.spondee.measure.us.fahrenheit
import inkapplications.spondee.measure.us.milesPerHour
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.degrees
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StationViewStateFactoryTest {
    val dummySummaryFactory = SummaryFactory(ParrotStringResources)
    val factoryWithNullMarkers = LogDetailsViewStateFactory(dummySummaryFactory, EpochFormatterFake, ParrotStringResources)
    val factoryWithDummyMarkers = LogDetailsViewStateFactory(dummySummaryFactory, EpochFormatterFake, ParrotStringResources)

    @Test
    fun nameFormatting() {
        val packet = PacketData.Unknown(body = "Test").toTestPacket().copy(
            route = testRoute.copy(source = StationAddress("TEST", "4"))
        ).toTestCapturedPacket()

        val result = factoryWithNullMarkers.create(LogDetailData(packet = packet))

        assertEquals("TEST-4", result.name)
    }

    @Test
    fun icons() {
        val base = PacketData.Unknown(
            body = "Test"
        ).toTestPacket().toTestCapturedPacket()

        val localResult = LogDetailData(packet = base.copy(origin = PacketOrigin.Local)).let(factoryWithNullMarkers::create)
        val isResult = LogDetailData(packet = base.copy(origin = PacketOrigin.AprsIs)).let(factoryWithNullMarkers::create)
        val ax25Result = LogDetailData(packet = base.copy(origin = PacketOrigin.Ax25)).let(factoryWithNullMarkers::create)

        assertEquals(Icons.Default.Storage, localResult.receiveIcon)
        assertEquals(Icons.Default.Cloud, isResult.receiveIcon)
        assertEquals(Icons.Default.SettingsInputAntenna, ax25Result.receiveIcon)
    }

    @Test
    fun timestamp() {
        val packet = PacketData.Unknown(body = "Test").toTestPacket().toTestCapturedPacket().copy(
            received = Instant.fromEpochMilliseconds(-22073104000)
        )

        val result = factoryWithNullMarkers.create(LogDetailData(packet = packet))

        assertEquals("-22073104000", result.timestamp)
    }

    @Test
    fun debugData() {
        val packet = PacketData.Unknown(
            body = "Test"
        ).toTestPacket().toTestCapturedPacket().copy(
            raw = "Test Debug".toByteArray()
        )
        val data = LogDetailData(
            packet = packet,
            debug = true
        )

        val result = factoryWithNullMarkers.create(data)

        assertEquals("Test Debug", result.rawSource)
    }

    @Test
    fun unknownPacket() {
        val packet = PacketData.Unknown(body = "Test").toTestPacket().copy(
            route = testRoute.copy(source = StationAddress("TEST", "4"))
        ).toTestCapturedPacket()

        val result = factoryWithNullMarkers.create(LogDetailData(packet = packet))

        assertFalse(result.mapable, "Not mapable for non-map data")
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
        val packet = PacketData.Weather(
            temperature = 72.fahrenheit,
            windData = WindData(
                direction = 12.degrees,
                speed = 34.milesPerHour,
                gust = 56.milesPerHour,
            )
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factoryWithNullMarkers.create(data)

        assertFalse(result.mapable, "Not mapable for non-map data")
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
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.0.latitude, 2.0.longitude),
            temperature = 72.fahrenheit,
            windData = WindData(12.degrees, 34.milesPerHour, 56.milesPerHour),
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factoryWithDummyMarkers.create(data)

        assertTrue(result.mapable, "Mapable weather data shows map")
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
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factoryWithDummyMarkers.create(data)

        assertTrue(result.mapable, "Mapable weather data shows map")
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
        val packet = PacketData.Position(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
            symbol = symbolOf('/', 'a'),
            comment = "test",
        ).toTestPacket().toTestCapturedPacket()
        val data = LogDetailData(
            packet = packet,
        )

        val result = factoryWithDummyMarkers.create(data)

        assertTrue(result.mapable, "Mapable position data shows map")
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertEquals("test", result.comment)
        assertNull(result.altitude, "No altitude for position packet")
        assertNull(result.telemetryValues, "No telemetry for position packet")
        assertNull(result.telemetrySequence, "No telemetry for position packet")
        assertNull(result.rawSource, "Debug data hidden")
    }
}
