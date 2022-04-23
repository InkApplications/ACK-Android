package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.android.capture.messages.ConversationData
import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class ConversationItemViewModelFactoryTest {
    @Test
    fun testConversationItem() {
        val station = "KE0YOF-2".toStationAddress()
        val message = PacketData.Message(
                addressee = station,
                message = "First!",
            )
            .toTestPacket()
            .toTestCapturedPacket()
            .copy(received = Instant.fromEpochMilliseconds(0))
        val conversation = ConversationData(
            selfCallsign = Callsign("KE0YOG"),
            latestMessage = message,
        )
        val factory = ConversationItemViewModelFactory()

        val viewModel = factory.create(conversation)

        assertEquals("First!", viewModel.messagePreview)
        assertEquals("KE0YOF", viewModel.name)
        assertEquals(station.callsign, viewModel.correspondent)
    }
}
