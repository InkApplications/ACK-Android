package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.testRoute
import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketOrigin
import com.inkapplications.ack.structures.AprsPacket
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
import kotlin.test.assertNull

class StationInsightViewStateFactoryTest {
    val dummySummaryFactory = SummaryFactory(ParrotStringResources)

    @Test
    fun unknownPacket() {
        val factory = StationInsightViewStateFactory(dummySummaryFactory)
        val packet = CapturedPacket(
            id = CaptureId(1),
            received = Instant.fromEpochMilliseconds(2),
            parsed = AprsPacket(
                route = testRoute.copy(
                    source = StationAddress("KE0YOG", "1"),
                ),
                data = PacketData.Unknown(
                    body = "test",
                ),
            ),
            origin = PacketOrigin.AprsIs,
            raw = byteArrayOf(),
        )
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )
        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertNull(result.comment, "No comment for unknown packet")
        assertNull(result.altitude, "No altitude for unknown packet")
        assertNull(result.telemetryValues, "No telemetry for unknown packet")
        assertNull(result.telemetrySequence, "No telemetry for unknown packet")
    }

    @Test
    fun positionlessWeatherPacket() {
        val factory = StationInsightViewStateFactory(dummySummaryFactory)
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
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertNull(result.comment, "No comment for positionless weather")
        assertNull(result.altitude, "No altitude for positionless weather")
        assertNull(result.telemetryValues, "No telemetry for positionless weather")
        assertNull(result.telemetrySequence, "No telemetry for positionless weather")
    }

    @Test
    fun weatherPacket() {
        val factory = StationInsightViewStateFactory(dummySummaryFactory)
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
        assertEquals("72ºF", result.temperature)
        assertEquals("12º|34mph|56mph", result.wind)
        assertNull(result.comment, "No comment for weather packet")
        assertNull(result.altitude, "No altitude for weather packet")
        assertNull(result.telemetryValues, "No telemetry for weather packet")
        assertNull(result.telemetrySequence, "No telemetry for weather packet")
    }

    @Test
    fun emptyWeatherPacket() {
        val factory = StationInsightViewStateFactory(dummySummaryFactory)
        val packet = PacketData.Weather(
            coordinates = GeoCoordinates(1.latitude, 2.longitude),
        ).toTestPacket().toTestCapturedPacket()
        val data = StationData(
            packets = listOf(packet),
            metric = false,
        )

        val result = factory.create(data)

        assertEquals("KE0YOG", result.name)
        assertNull(result.temperature, "No temperature for empty weather packet")
        assertNull(result.wind, "No wind data for empty non weather packet")
        assertNull(result.comment, "No comment for unknown packet")
        assertNull(result.altitude, "No altitude for unknown packet")
        assertNull(result.telemetryValues, "No telemetry for unknown packet")
        assertNull(result.telemetrySequence, "No telemetry for unknown packet")
    }

    @Test
    fun positionPacket() {
        val factory = StationInsightViewStateFactory(dummySummaryFactory)
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
        assertNull(result.temperature, "No temperature for non weather packet")
        assertNull(result.wind, "No wind data for non weather packet")
        assertEquals("test", result.comment)
        assertNull(result.altitude, "No altitude for position packet")
        assertNull(result.telemetryValues, "No telemetry for position packet")
        assertNull(result.telemetrySequence, "No telemetry for position packet")
    }
}
