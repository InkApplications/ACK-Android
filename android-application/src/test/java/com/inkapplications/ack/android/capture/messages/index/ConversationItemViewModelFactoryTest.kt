package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class ConversationItemViewModelFactoryTest {
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
        val factory = ConversationItemViewModelFactory()

        val viewModel = factory.create(station.callsign to listOf(message1, message2))

        assertEquals("Second!", viewModel.messagePreview)
        assertEquals("KE0YOF", viewModel.name)
        assertEquals(station.callsign, viewModel.idCallsign)
    }
}
