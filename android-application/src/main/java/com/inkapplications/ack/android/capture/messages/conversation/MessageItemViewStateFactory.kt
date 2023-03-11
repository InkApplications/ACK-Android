package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.Alignment
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.ViewStateFactory
import com.inkapplications.android.extensions.format.DateTimeFormatter
import dagger.Reusable
import javax.inject.Inject

@Reusable
class MessageItemViewStateFactory @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
    private val stringResources: StringResources,
): ViewStateFactory<MessageData, MessageItemViewState> {
    override fun create(data: MessageData): MessageItemViewState {
        return MessageItemViewState(
            message = (data.message.parsed.data as PacketData.Message).message,
            timestamp = dateTimeFormatter.formatTimestamp(data.message.received),
            alignment = if (data.selfCallsign == data.message.parsed.route.source.callsign) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            },
            icon = when (data.message.source) {
                PacketSource.Ax25 -> Icons.Default.SettingsInputAntenna
                PacketSource.AprsIs -> Icons.Default.Cloud
                PacketSource.Local -> Icons.Default.Storage
            },
            iconDescription = when(data.message.source) {
                PacketSource.Ax25 -> stringResources.getString(R.string.messages_item_icon_ax25_description)
                PacketSource.AprsIs -> stringResources.getString(R.string.messages_item_icon_internet_description)
                PacketSource.Local -> stringResources.getString(R.string.messages_item_icon_local_description)
            },
        )
    }
}
