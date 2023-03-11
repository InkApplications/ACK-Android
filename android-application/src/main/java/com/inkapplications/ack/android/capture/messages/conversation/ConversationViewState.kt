package com.inkapplications.ack.android.capture.messages.conversation

sealed interface ConverstationViewState {
    val title: String

    data class Initial(override val title: String): ConverstationViewState
    data class MessageList(
        override val title: String,
        val messages: List<MessageItemViewState>,
    ): ConverstationViewState
}
