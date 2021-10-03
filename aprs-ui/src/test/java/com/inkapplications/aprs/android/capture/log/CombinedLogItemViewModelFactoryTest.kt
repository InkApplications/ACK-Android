package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.TestPackets
import com.inkapplications.aprs.android.symbol.SymbolFactoryStub
import inkapplications.spondee.measure.Fahrenheit
import kotlin.test.Test
import kotlin.test.assertEquals

class CombinedLogItemViewModelFactoryTest {
    val factory = CombinedLogItemViewModelFactory(SymbolFactoryStub)

    @Test
    fun unknownPacket() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.unknown,
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
            packet = TestPackets.position,
            metric = false,
        )

        assertEquals("Position: Test Position", result.comment)
    }

    @Test
    fun positionGeneric() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.position.copy(comment = ""),
            metric = false,
        )

        assertEquals("Position", result.comment)
    }

    @Test
    fun weather() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.weather.copy(
                temperature = Fahrenheit.of(72),
            ),
            metric = false,
        )

        assertEquals("Weather: 72ºF", result.comment)
    }

    @Test
    fun weatherGeneric() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.weather,
            metric = false,
        )

        assertEquals("Weather", result.comment)
    }

    @Test
    fun objectReport() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.objectReport,
            metric = false,
        )

        assertEquals("Object: Test Object - Hello World", result.comment)
    }

    @Test
    fun objectReportNoComment() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.objectReport.copy(
                comment = " ",
            ),
            metric = false,
        )

        assertEquals("Object: Test Object", result.comment)
    }

    @Test
    fun itemReport() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.itemReport,
            metric = false,
        )

        assertEquals("Item: Test Item - Hello World", result.comment)
    }

    @Test
    fun itemReportNoComment() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.itemReport.copy(
                comment = " ",
            ),
            metric = false,
        )

        assertEquals("Item: Test Item", result.comment)
    }

    @Test
    fun message() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.message,
            metric = false,
        )

        assertEquals("[KE0YOG-2] Hello KE0YOG", result.comment)
    }

    @Test
    fun messageWithCount() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.message.copy(
                messageNumber = 69,
            ),
            metric = false,
        )

        assertEquals("[KE0YOG-2] Hello KE0YOG (69)", result.comment)
    }

    @Test
    fun telemetryReport() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.telemetryReport,
            metric = false,
        )

        assertEquals("Telemetry: Hello World", result.comment)
    }

    @Test
    fun telemetryReportGeneric() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.telemetryReport.copy(
                comment = "",
            ),
            metric = false,
        )

        assertEquals("Telemetry", result.comment)
    }

    @Test
    fun statusReport() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.statusReport,
            metric = false,
        )

        assertEquals("Status: Testing", result.comment)
    }

    @Test
    fun capabilityReport() {
        val result = factory.create(
            id = 123L,
            packet = TestPackets.capabilityReport,
            metric = false,
        )

        assertEquals("Capability Report", result.comment)
    }
}
