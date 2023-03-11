package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Create the viewmodel for items in a list of Message conversations.
 */
@Reusable
class ConversationItemViewStateFactory @Inject constructor(): ViewStateFactory<MessageData, ConversationItemViewState> {
    override fun create(data: MessageData): ConversationItemViewState {
        val message = data.message.parsed.data as PacketData.Message
        val correspondent = message.addressee.takeIf { it.callsign != data.selfCallsign } ?: data.message.parsed.route.source
        return ConversationItemViewState(
            name = correspondent.callsign.canonical,
            messagePreview = message.message,
            correspondent = correspondent.callsign,
        )
    }
}
