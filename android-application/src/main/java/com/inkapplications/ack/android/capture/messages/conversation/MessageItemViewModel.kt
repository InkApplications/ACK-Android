package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.ui.Alignment


data class MessageItemViewModel(
    val message: String,
    val timestamp: String,
    val alignment: Alignment,
)
