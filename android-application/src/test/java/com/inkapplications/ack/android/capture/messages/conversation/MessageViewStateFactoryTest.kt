package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.Alignment
import com.inkapplications.ack.android.EpochFormatterStub
import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.toStationAddress
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageViewStateFactoryTest {
    private val factory = MessageItemViewStateFactory(EpochFormatterStub, ParrotStringResources)
    private val message = PacketData.Message(
        addressee = "KE0YOF".toStationAddress(),
        message = "Hello World!",
    )
    val capturedPacket = message.toTestPacket()
        .toTestCapturedPacket()
        .copy(
            received = Instant.fromEpochMilliseconds(12345)
        )

    @Test
    fun testOutgoingMessage() {
        val packetData = capturedPacket.let { MessageData(Callsign("KE0YOG"), it) }

        val viewModel = factory.create(packetData)

        assertEquals("Hello World!", viewModel.message)
        assertEquals("12345", viewModel.timestamp)
        assertEquals(Alignment.CenterEnd, viewModel.alignment)
    }

    @Test
    fun testIncomingMessageItem() {
        val packetData = capturedPacket.let { MessageData(Callsign("KE0YOF"), it) }
        val viewModel = factory.create(packetData)

        assertEquals("Hello World!", viewModel.message)
        assertEquals("12345", viewModel.timestamp)
        assertEquals(Alignment.CenterStart, viewModel.alignment)
    }

    @Test
    fun testIcons() {
        val local = capturedPacket.copy(
            source = PacketSource.Local,
        ).let { MessageData(Callsign("KE0YOG"), it) }.run(factory::create)
        val radio = capturedPacket.copy(
            source = PacketSource.Ax25,
        ).let { MessageData(Callsign("KE0YOG"), it) }.run(factory::create)
        val internet = capturedPacket.copy(
            source = PacketSource.AprsIs,
        ).let { MessageData(Callsign("KE0YOG"), it) }.run(factory::create)

        assertEquals(Icons.Default.Storage, local.icon)
        assertEquals(Icons.Default.SettingsInputAntenna, radio.icon)
        assertEquals(Icons.Default.Cloud, internet.icon)
    }
}
