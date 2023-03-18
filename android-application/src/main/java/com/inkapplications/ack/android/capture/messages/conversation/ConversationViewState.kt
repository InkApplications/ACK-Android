package com.inkapplications.ack.android.capture.messages.conversation

/**
 * States for a single conversation view.
 */
sealed interface ConversationViewState {
    /**
     * Title of the page to display in navigation.
     */
    val title: String

    /**
     * Indicates that no data has been loaded yet.
     */
    data class Initial(override val title: String): ConversationViewState

    /**
     * Indicates that data has been loaded, but there are no messages yet.
     */
    data class Empty(override val title: String): ConversationViewState

    /**
     * Message data for the conversation.
     *
     * @param messages The list of message history.
     */
    data class MessageList(
        override val title: String,
        val messages: List<MessageItemState>,
    ): ConversationViewState
}
