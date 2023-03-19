package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessageIndexStateFactoryTest {
    private val factory = MessageIndexStateFactory()

    @Test
    fun empty() {
        val result = factory.createScreenState(
            latestMessages = emptyList()
        )

        assertTrue(result is MessageIndexState.Empty)
    }

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
        val conversation = MessageData(
            selfCallsign = Callsign("KE0YOG"),
            message = message,
        )

        val results = factory.createScreenState(listOf(conversation))
        assertTrue(results is MessageIndexState.ConversationList)
        assertEquals(1, results.conversations.size)

        val result = results.conversations.single()
        assertEquals("First!", result.messagePreview)
        assertEquals("KE0YOF", result.name)
        assertEquals(station.callsign, result.correspondent)
    }
}
