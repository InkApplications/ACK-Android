package com.inkapplications.ack.android.capture.messages.conversation

interface ConversationController {
    fun onNavigateUpPressed()
    fun onSendMessage(message: String)
}