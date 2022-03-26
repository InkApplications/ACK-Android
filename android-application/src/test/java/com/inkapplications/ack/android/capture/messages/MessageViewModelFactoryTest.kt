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
    @Test
    fun testMessage() {
        val message = PacketData.Message(
            addressee = "KE0YOF".toStationAddress(),
            message = "Hello World!",
        )
        val capturedPacket = message.toTestPacket().toTestCapturedPacket().copy(
            received = Instant.fromEpochMilliseconds(-22073104000)
        )

        val viewModel = MessageViewModelFactory(
            timeZone = TimeZone.UTC,
        ).createMessageItem(capturedPacket)

        assertEquals("Hello World!", viewModel.message)
        assertEquals("1969-04-20 12:34", viewModel.timestamp)
    }
}
