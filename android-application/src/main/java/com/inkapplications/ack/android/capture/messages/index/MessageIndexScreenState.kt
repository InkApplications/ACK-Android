package com.inkapplications.ack.android.capture.messages.index

sealed interface MessageIndexScreenState {
    object Initial: MessageIndexScreenState
    object Empty: MessageIndexScreenState
    data class ConversationList(
        val conversations: List<ConversationItemViewModel>,
    ): MessageIndexScreenState
}
