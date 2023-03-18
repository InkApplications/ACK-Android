package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * State model for a single message in a conversation.
 */
data class MessageItemState(
    /**
     * The content of the message that was sent.
     */
    val message: String,

    /**
     * Readable timestamp in local time.
     */
    val timestamp: String,

    /**
     * Alignment of the text bubble based on the sender.
     */
    val alignment: Alignment,

    /**
     * State icon used to indicate how the message was sent.
     */
    val icon: ImageVector,

    /**
     * Content description used for [icon]'s accessibility.
     */
    val iconDescription: String,
)
