package com.inkapplications.aprs.android

import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.aprs.data.PacketSource
import com.inkapplications.karps.structures.*
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.datetime.Instant

object TestPackets {
    val unknown = AprsPacket.Unknown(
        raw = "unknown packet data",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '?',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        body = "Unknown Packet Data"
    )
    val weather = AprsPacket.Weather(
        raw = "test weather packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        timestamp = Instant.DISTANT_PAST,
        windData = WindData(null, null, null),
        precipitation = Precipitation(),
        coordinates = null,
        symbol = null,
        temperature = null,
        humidity = null,
        pressure = null,
        irradiance = null,
    )
    val position = AprsPacket.Position(
        raw = "test position packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        timestamp = Instant.DISTANT_PAST,
        coordinates = GeoCoordinates(123.0.latitude, 456.0.longitude),
        symbol = Symbol.Primary('a'),
        comment = "Test Position",
        altitude = null,
        trajectory = null,
        range = null,
        transmitterInfo = null,
        signalInfo = null,
        directionReportExtra = null,
    )
    val objectReport = AprsPacket.ObjectReport(
        raw = "test object packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        timestamp = Instant.DISTANT_PAST,
        coordinates = GeoCoordinates(123.0.latitude, 456.0.longitude),
        symbol = Symbol.Primary('a'),
        comment = "Hello World",
        altitude = null,
        trajectory = null,
        range = null,
        transmitterInfo = null,
        signalInfo = null,
        directionReportExtra = null,
        name = "Test Object",
        state = ReportState.Live,
    )
    val itemReport = AprsPacket.ItemReport(
        raw = "test position packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        coordinates = GeoCoordinates(123.0.latitude, 456.0.longitude),
        symbol = Symbol.Primary('a'),
        comment = "Hello World",
        altitude = null,
        trajectory = null,
        range = null,
        transmitterInfo = null,
        signalInfo = null,
        directionReportExtra = null,
        name = "Test Item",
        state = ReportState.Live,
    )
    val message = AprsPacket.Message(
        raw = "test position packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        addressee = Address("KE0YOG", "2"),
        message = "Hello KE0YOG",
        messageNumber = null,
    )
    val telemetryReport = AprsPacket.TelemetryReport(
        raw = "test position packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        comment = "Hello World",
        sequenceId = "1",
        data = TelemetryValues(1f, 2f, 3f, 4f, 5f, 123.toUByte()),
    )
    val statusReport = AprsPacket.StatusReport(
        raw = "test position packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        time = null,
        status = "Testing",
    )
    val capabilityReport = AprsPacket.CapabilityReport(
        raw = "test position packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG", "1"),
        digipeaters = listOf(),
        capabilityData = setOf(),
    )

    fun AprsPacket.toTestCapturedPacket() = CapturedPacket(
        id = 1,
        received = 0,
        data = this,
        source = PacketSource.AprsIs,
    )
}
