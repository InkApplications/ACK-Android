package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

data class MessageItemViewModel(
    val message: String,
    val timestamp: String,
    val alignment: Alignment,
    val icon: ImageVector,
    val iconDescription: String,
)
