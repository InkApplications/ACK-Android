package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.android.symbol.SymbolFactoryStub
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.karps.structures.*
import inkapplications.spondee.measure.Fahrenheit
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlin.test.Test
import kotlin.test.assertEquals

class CombinedLogItemViewModelFactoryTest {
    val factory = CombinedLogItemViewModelFactory(SymbolFactoryStub)

    @Test
    fun unknownPacket() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Unknown(
                body = "",
            ).toTestPacket(),
            metric = false,
        )

        assertEquals(123L, result.id)
        assertEquals("KE0YOG", result.origin)
        assertEquals("Unknown data", result.comment)
    }

    @Test
    fun position() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Position(
                coordinates = GeoCoordinates(12f.latitude, 34f.longitude),
                symbol = symbolOf('/', '-'),
                comment = "Test Position",
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Position: Test Position", result.comment)
    }

    @Test
    fun positionGeneric() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Position(
                comment = "",
                coordinates = GeoCoordinates(12f.latitude, 34f.longitude),
                symbol = symbolOf('/', '-'),
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Position", result.comment)
    }

    @Test
    fun weather() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Weather(
                temperature = Fahrenheit.of(72),
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Weather: 72ºF", result.comment)
    }

    @Test
    fun weatherGeneric() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Weather().toTestPacket(),
            metric = false,
        )

        assertEquals("Weather", result.comment)
    }

    @Test
    fun objectReport() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.ObjectReport(
                coordinates = GeoCoordinates(12f.latitude, 34f.longitude),
                symbol = symbolOf('/', '-'),
                comment = "Hello World",
                name = "Test Object",
                state = ReportState.Live,
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Object: Test Object - Hello World", result.comment)
    }

    @Test
    fun objectReportNoComment() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.ObjectReport(
                coordinates = GeoCoordinates(12f.latitude, 34f.longitude),
                symbol = symbolOf('/', '-'),
                name = "Test Object",
                comment = " ",
                state = ReportState.Live,
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Object: Test Object", result.comment)
    }

    @Test
    fun itemReport() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.ItemReport(
                coordinates = GeoCoordinates(12f.latitude, 34f.longitude),
                symbol = symbolOf('/', '-'),
                name = "Test Item",
                comment = "Hello World",
                state = ReportState.Live,
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Item: Test Item - Hello World", result.comment)
    }

    @Test
    fun itemReportNoComment() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.ItemReport(
                coordinates = GeoCoordinates(12f.latitude, 34f.longitude),
                symbol = symbolOf('/', '-'),
                name = "Test Item",
                comment = " ",
                state = ReportState.Live,
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Item: Test Item", result.comment)
    }

    @Test
    fun message() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Message(
                addressee = Address("KE0YOG-2"),
                message = "Hello KE0YOG",
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("[KE0YOG-2] Hello KE0YOG", result.comment)
    }

    @Test
    fun messageWithCount() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.Message(
                addressee = Address("KE0YOG-2"),
                message = "Hello KE0YOG",
                messageNumber = 69,
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("[KE0YOG-2] Hello KE0YOG (69)", result.comment)
    }

    @Test
    fun telemetryReport() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.TelemetryReport(
                sequenceId = "",
                data = TelemetryValues(1f, 2f, 3f, 4f, 5f, 0u),
                comment = "Hello World",
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Telemetry: Hello World", result.comment)
    }

    @Test
    fun telemetryReportGeneric() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.TelemetryReport(
                sequenceId = "",
                data = TelemetryValues(1f, 2f, 3f, 4f, 5f, 0u),
                comment = " ",
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Telemetry", result.comment)
    }

    @Test
    fun statusReport() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.StatusReport(
                status = "Testing",
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Status: Testing", result.comment)
    }

    @Test
    fun capabilityReport() {
        val result = factory.create(
            id = 123L,
            packet = PacketData.CapabilityReport(
                capabilityData = setOf(),
            ).toTestPacket(),
            metric = false,
        )

        assertEquals("Capability Report", result.comment)
    }
}
