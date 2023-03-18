package com.inkapplications.ack.android.capture.messages.conversation

/**
 * Actions that can be taken from the conversation screen.
 */
interface ConversationController {
    /**
     * Invoked when the user presses the back/up button on the navigation bar.
     */
    fun onNavigateUpPressed()

    /**
     * Invoked when the user presses the send button in the message box.
     *
     * @param message The text the user has entered in the message box.
     */
    fun onSendMessage(message: String)
}
