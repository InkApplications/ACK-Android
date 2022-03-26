package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import dagger.Reusable
import kotlinx.datetime.TimeZone
import java.text.SimpleDateFormat
import javax.inject.Inject

@Reusable
class MessageViewModelFactory @Inject constructor(
    timeZone: TimeZone,
) {
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm").apply {
        this.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }

    fun createMessageItem(packet: CapturedPacket): MessageItemViewModel {
        return MessageItemViewModel(
            message = (packet.parsed.data as PacketData.Message).message,
            timestamp = timestampFormat.format(packet.received.toEpochMilliseconds())
        )
    }

    fun createConversationItem(callsign: Callsign, messages: List<CapturedPacket>): ConversationItemViewModel {
        return ConversationItemViewModel(
            name = callsign.canonical,
            messagePreview = (messages.last().parsed.data as PacketData.Message).message,
            idCallsign = callsign,
        )
    }
}
