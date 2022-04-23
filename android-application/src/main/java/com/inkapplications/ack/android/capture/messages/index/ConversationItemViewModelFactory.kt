package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.android.capture.messages.ConversationData
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.ViewModelFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Create the viewmodel for items in a list of Message conversations.
 */
@Reusable
class ConversationItemViewModelFactory @Inject constructor(): ViewModelFactory<ConversationData, ConversationItemViewModel> {
    override fun create(data: ConversationData): ConversationItemViewModel {
        val message = data.latestMessage.parsed.data as PacketData.Message
        val correspondent = message.addressee.takeIf { it.callsign != data.selfCallsign } ?: data.latestMessage.parsed.route.source
        return ConversationItemViewModel(
            name = correspondent.callsign.canonical,
            messagePreview = message.message,
            correspondent = correspondent.callsign,
        )
    }
}
