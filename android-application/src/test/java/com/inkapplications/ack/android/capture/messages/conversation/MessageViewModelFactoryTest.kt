package com.inkapplications.ack.android.capture.messages.conversation

import com.inkapplications.ack.android.EpochFormatterStub
import com.inkapplications.ack.android.capture.messages.conversation.MessageItemViewModelFactory
import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageViewModelFactoryTest {
    @Test
    fun testMessageItem() {
        val factory = MessageItemViewModelFactory(EpochFormatterStub)
        val message = PacketData.Message(
            addressee = "KE0YOF".toStationAddress(),
            message = "Hello World!",
        )
        val capturedPacket = message.toTestPacket().toTestCapturedPacket().copy(
            received = Instant.fromEpochMilliseconds(12345)
        )

        val viewModel = factory.create(capturedPacket)

        assertEquals("Hello World!", viewModel.message)
        assertEquals("12345", viewModel.timestamp)
    }
}
