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
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.format.DateTimeFormatter
import dagger.Reusable
import javax.inject.Inject

/**
 * Creates Conversation state models based on a list of message packets.
 */
@Reusable
class ConversationViewStateFactory @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
    private val stringResources: StringResources,
) {
    /**
     * Create the initial viewstate for the view.
     *
     * @param adressee The callsign of the station this conversation is with.
     */
    fun createInitial(adressee: Callsign): ConversationViewState {
        return ConversationViewState.Initial(
            title = createTitle(adressee),
        )
    }

    /**
     * Create a message list state model.
     *
     * @param adressee The callsign of the station this conversation is with.
     * @param messages a list of message objectsto be displayed in the conversation.
     */
    fun createMessageList(
        addressee: Callsign,
        messages: List<MessageData>
    ): ConversationViewState {
        val title = createTitle(addressee)

        return when {
            messages.isEmpty() -> ConversationViewState.Empty(
                title = title
            )
            else -> ConversationViewState.MessageList(
                title = title,
                messages = messages.map { createMessageView(it) },
            )
        }
    }

    /**
     * Create a single message item's state model.
     *
     * @param data The message data to render into the state model
     */
    private fun createMessageView(data: MessageData): MessageItemState {
        return MessageItemState(
            message = (data.message.parsed.data as PacketData.Message).message,
            timestamp = dateTimeFormatter.formatTimestamp(data.message.received),
            alignment = if (data.isOutgoing) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            },
            icon = when (data.message.source) {
                PacketSource.Ax25 -> Icons.Default.SettingsInputAntenna
                PacketSource.AprsIs -> Icons.Default.Cloud
                PacketSource.Local -> Icons.Default.Storage
            },
            iconDescription = when (data.message.source) {
                PacketSource.Ax25 -> stringResources.getString(R.string.messages_item_icon_ax25_description)
                PacketSource.AprsIs -> stringResources.getString(R.string.messages_item_icon_internet_description)
                PacketSource.Local -> stringResources.getString(R.string.messages_item_icon_local_description)
            },
        )
    }

    /**
     * Create a readable title from a callsign object.
     *
     * @param callsign The callsign to be displayed.
     */
    private fun createTitle(callsign: Callsign): String = callsign.canonical
}