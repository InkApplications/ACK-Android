package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.map.MarkerViewState
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.station.StationAddress
import inkapplications.spondee.measure.us.fahrenheit
import inkapplications.spondee.measure.us.milesPerHour
import inkapplications.spondee.spatial.*
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.*

class StationViewStateFactoryTest {
    val dummySummaryFactory = SummaryFactory(ParrotStringResources)
    val dummyMarker = MarkerViewState(0, GeoCoordinates(0.latitude, 0.longitude), null)

    @Test
    fun unknownPacket() {
        val factory = StationInsightViewStateFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val packet = CapturedPacket(
            id = 1,
            received = Instant.fromEpochMilliseconds(2),
            parsed = AprsPacket(
                route = testRoute.copy(
                    source = StationAddress("KE0YOG", "1"),
                ),
                data = PacketData.Unknown(
                    body = "test",
                ),
            ),
            source = PacketSource.AprsIs,
            raw = byteArrayOf(),
        )
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )
        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertTrue(result.markers.isEmpty(), "No map markers for unknown packet")
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertNull(result.comment, "No comment for unknown packet")
        assertNull(result.altitude, "No altitude for unknown packet")
        assertNull(result.telemetryValues, "No telemetry for unknown packet")
        assertNull(result.telemetrySequence, "No telemetry for unknown packet")
    }

    @Test
    fun positionlessWeatherPacket() {
        val factory = StationInsightViewStateFactory(NullMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Weather(
            temperature = 72.fahrenheit,
            windData = WindData(
                direction = 12.degrees,
                speed = 34.milesPerHour,
                gust = 56.milesPerHour,
            )
        ).toTestPacket().toTestCapturedPacket()
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )

        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertTrue(result.markers.isEmpty(), "No map markers for positionless weather")
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertNull(result.comment, "No comment for positionless weather")
        assertNull(result.altitude, "No altitude for positionless weather")
        assertNull(result.telemetryValues, "No telemetry for positionless weather")
        assertNull(result.telemetrySequence, "No telemetry for positionless weather")
    }

    @Test
    fun weatherPacket() {
        val factory = StationInsightViewStateFactory(DummyMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.0.latitude, 2.0.longitude),
            temperature = 72.fahrenheit,
            windData = WindData(12.degrees, 34.milesPerHour, 56.milesPerHour),
        ).toTestPacket().toTestCapturedPacket()
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )

        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertEquals(1, result.markers.size)
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertNull(result.comment, "No comment for weather packet")
        assertNull(result.altitude, "No altitude for weather packet")
        assertNull(result.telemetryValues, "No telemetry for weather packet")
        assertNull(result.telemetrySequence, "No telemetry for weather packet")
    }

    @Test
    fun emptyWeatherPacket() {
        val factory = StationInsightViewStateFactory(DummyMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
        ).toTestPacket().toTestCapturedPacket()
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )

        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertEquals(1, result.markers.size)
        assertNull(result.temperature, "No temperature for empty weather packet")
        assertNull(result.wind, "No wind data for empty non weather packet")
        assertNull(result.comment, "No comment for unknown packet")
        assertNull(result.altitude, "No altitude for unknown packet")
        assertNull(result.telemetryValues, "No telemetry for unknown packet")
        assertNull(result.telemetrySequence, "No telemetry for unknown packet")
    }

    @Test
    fun positionPacket() {
        val factory = StationInsightViewStateFactory(DummyMarkerFactoryMock, dummySummaryFactory)
        val packet = PacketData.Position(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
            symbol = symbolOf('/', 'a'),
            comment = "test",
        ).toTestPacket().toTestCapturedPacket()
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )

        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertEquals(1, result.markers.size)
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertEquals("test", result.comment)
        assertNull(result.altitude, "No altitude for position packet")
        assertNull(result.telemetryValues, "No telemetry for position packet")
        assertNull(result.telemetrySequence, "No telemetry for position packet")
    }
}
