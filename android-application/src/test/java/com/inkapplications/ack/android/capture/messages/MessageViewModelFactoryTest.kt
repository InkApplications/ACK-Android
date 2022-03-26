package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageViewModelFactoryTest {
    private val factory = MessageViewModelFactory(
        timeZone = TimeZone.UTC,
    )

    @Test
    fun testMessageItem() {
        val message = PacketData.Message(
            addressee = "KE0YOF".toStationAddress(),
            message = "Hello World!",
        )
        val capturedPacket = message.toTestPacket().toTestCapturedPacket().copy(
            received = Instant.fromEpochMilliseconds(-22073104000)
        )

        val viewModel = factory.createMessageItem(capturedPacket)

        assertEquals("Hello World!", viewModel.message)
        assertEquals("1969-04-20 12:34", viewModel.timestamp)
    }

    @Test
    fun testConversationItem() {
        val station = "KE0YOF-2".toStationAddress()
        val message1 = PacketData.Message(
                addressee = station,
                message = "First!",
            )
            .toTestPacket()
            .toTestCapturedPacket()
            .copy(received = Instant.fromEpochMilliseconds(0))
        val message2 = PacketData.Message(
                addressee = station,
                message = "Second!",
            )
            .toTestPacket()
            .toTestCapturedPacket()
            .copy(received = Instant.fromEpochMilliseconds(-22073104000))

        val viewModel = factory.createConversationItem(station.callsign, listOf(message1, message2))

        assertEquals("Second!", viewModel.messagePreview)
        assertEquals("KE0YOF", viewModel.name)
        assertEquals(station.callsign, viewModel.idCallsign)
    }
}
