package com.inkapplications.ack.android.capture.messages

sealed interface ConverstationViewState {
    val title: String

    data class Initial(override val title: String): ConverstationViewState
    data class MessageList(
        override val title: String,
        val messages: List<MessageItemViewModel>,
    ): ConverstationViewState
}
