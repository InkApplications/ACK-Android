package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.Alignment
import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConversationViewStateFactoryTest {
    private val factory = ConversationViewStateFactory(EpochFormatterFake, ParrotStringResources)

    private val me = "KE0YOG".toStationAddress()
    private val addressee = "KE0YOF".toStationAddress()

    @Test
    fun initial() {
        val result = factory.createInitial(addressee.callsign)

        assertEquals("KE0YOF", result.title)
    }

    @Test
    fun emptyMessageList() {
        val result = factory.createMessageList(addressee.callsign, emptyList(), DriverConnectionState.Disconnected, DriverSelection.Audio)

        assertTrue(result is ConversationViewState.Empty)
        assertEquals("KE0YOF", result.title)
        assertEquals(false, result.sendEnabled)
    }

    @Test
    fun connected() {
        val result = factory.createMessageList(addressee.callsign, emptyList(), DriverConnectionState.Connected, DriverSelection.Audio)
        assertEquals(true, result.sendEnabled)
    }

    @Test
    fun testOutgoingMessage() {
        val packet = PacketData.Message(
            addressee = addressee,
            message = "Hello World!",
        ).toTestPacket().copy(
            route = testRoute.copy(
                source = me,
            )
        ).toTestCapturedPacket().copy(
            received = Instant.fromEpochMilliseconds(12345),
        )

        val messageResult = getMessageState(packet)
        assertEquals("Hello World!", messageResult.message)
        assertEquals("12345", messageResult.timestamp)
        assertEquals(Alignment.CenterEnd, messageResult.alignment)
    }

    @Test
    fun testIncomingMessageItem() {
        val packet = PacketData.Message(
            addressee = me,
            message = "Hello World!",
        ).toTestPacket().copy(
            route = testRoute.copy(
                source = addressee,
            )
        ).toTestCapturedPacket().copy(
            received = Instant.fromEpochMilliseconds(12345),
        )

        val messageResult = getMessageState(packet)

        assertEquals("Hello World!", messageResult.message)
        assertEquals("12345", messageResult.timestamp)
        assertEquals(Alignment.CenterStart, messageResult.alignment)
    }

    private fun getMessageState(packet: CapturedPacket): MessageItemState {
        val data = MessageData(me.callsign, packet)
        val result = factory.createMessageList(addressee.callsign, listOf(data), DriverConnectionState.Disconnected, DriverSelection.Audio)

        assertTrue(result is ConversationViewState.MessageList)
        assertEquals(1, result.messages.size)

        return result.messages.single()
    }

    @Test
    fun testIcons() {
        val basePacket = PacketData.Message(
            addressee = addressee,
            message = "test",
        ).toTestPacket().toTestCapturedPacket()
        val local = getMessageState(basePacket.copy(
            source = PacketSource.Local,
        ))
        val radio = getMessageState(basePacket.copy(
            source = PacketSource.Ax25,
        ))
        val internet = getMessageState(basePacket.copy(
            source = PacketSource.AprsIs,
        ))

        assertEquals(Icons.Default.Storage, local.icon)
        assertEquals(Icons.Default.SettingsInputAntenna, radio.icon)
        assertEquals(Icons.Default.Cloud, internet.icon)
    }
}
