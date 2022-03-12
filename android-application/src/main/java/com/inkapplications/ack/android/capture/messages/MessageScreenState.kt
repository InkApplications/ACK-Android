package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.data.CapturedPacket

sealed interface MessageScreenState {
    object Initial: MessageScreenState
    object Empty: MessageScreenState
    data class MessageList(
        val messages: List<CapturedPacket>
    ): MessageScreenState
}
