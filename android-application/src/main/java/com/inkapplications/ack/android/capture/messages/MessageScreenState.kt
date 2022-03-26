package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.structures.station.Callsign

sealed interface MessageScreenState {
    object Initial: MessageScreenState
    object Empty: MessageScreenState
    data class ConversationList(
        val conversations: List<ConversationViewModel>,
    ): MessageScreenState
}

data class ConversationViewModel(
    val name: String,
    val messagePreview: String,
    val idCallsign: Callsign,
)
