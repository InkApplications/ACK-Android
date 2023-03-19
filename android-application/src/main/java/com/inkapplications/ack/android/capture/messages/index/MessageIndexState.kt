package com.inkapplications.ack.android.capture.messages.index

/**
 * State of the list of conversations screen.
 */
sealed interface MessageIndexState {
    /**
     * Indicates that no data has been loaded yet.
     */
    object Initial: MessageIndexState

    /**
     * State that indicates that data is loaded, but no messages have been
     * sent or received.
     */
    object Empty: MessageIndexState

    /**
     * Loaded data to display a list of conversations.
     */
    data class ConversationList(
        /**
         * Conversation rows to display.
         */
        val conversations: List<ConversationItemState>,
    ): MessageIndexState
}
