package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.android.capture.messages.MessageData
import com.inkapplications.ack.structures.PacketData
import dagger.Reusable
import javax.inject.Inject

/**
 * Create the state models for a list of message conversations.
 */
@Reusable
class MessageIndexStateFactory @Inject constructor() {
    /**
     * Create a state object for the conversation list.
     *
     * @param latestMessages A list of the latest messages in each conversation.
     */
    fun createScreenState(latestMessages: List<MessageData>): MessageIndexState {
        return when {
            latestMessages.isEmpty() -> MessageIndexState.Empty
            else -> MessageIndexState.ConversationList(
                conversations = latestMessages.map(::createConversationItem),
            )
        }
    }

    /**
     * Create a single conversation state model from its latest message.
     *
     * @param data The latest message in this conversation.
     */
    private fun createConversationItem(data: MessageData): ConversationItemState {
        val message = data.message.parsed.data as PacketData.Message
        val correspondent = message.addressee.takeIf { it.callsign != data.selfCallsign } ?: data.message.parsed.route.source
        return ConversationItemState(
            name = correspondent.callsign.canonical,
            messagePreview = message.message,
            correspondent = correspondent.callsign,
        )
    }
}
