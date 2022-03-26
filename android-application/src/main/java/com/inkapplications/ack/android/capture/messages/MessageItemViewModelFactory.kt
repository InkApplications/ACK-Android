package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.ViewModelFactory
import com.inkapplications.android.extensions.format.DateTimeFormatter
import dagger.Reusable
import java.text.SimpleDateFormat
import javax.inject.Inject

@Reusable
class MessageItemViewModelFactory @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
): ViewModelFactory<CapturedPacket, MessageItemViewModel> {
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm").apply {
        this.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }

    override fun create(data: CapturedPacket): MessageItemViewModel {
        return MessageItemViewModel(
            message = (data.parsed.data as PacketData.Message).message,
            timestamp = dateTimeFormatter.formatTimestamp(data.received)
        )
    }
}
