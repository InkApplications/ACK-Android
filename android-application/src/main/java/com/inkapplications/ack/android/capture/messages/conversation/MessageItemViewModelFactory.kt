package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.ui.Alignment
import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.ViewModelFactory
import com.inkapplications.android.extensions.format.DateTimeFormatter
import dagger.Reusable
import java.text.SimpleDateFormat
import javax.inject.Inject

@Reusable
class MessageItemViewModelFactory @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
): ViewModelFactory<MessageData, MessageItemViewModel> {
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm").apply {
        this.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }

    override fun create(data: MessageData): MessageItemViewModel {
        return MessageItemViewModel(
            message = (data.message.parsed.data as PacketData.Message).message,
            timestamp = dateTimeFormatter.formatTimestamp(data.message.received),
            alignment = if (data.selfCallsign == data.message.parsed.route.source.callsign) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            }
        )
    }
}
