package com.inkapplications.ack.android.capture.messages.create

/**
 * Actions invoked on the new message screen
 */
interface CreateConversationController {
    /**
     * Invoked when the user clicks the "start messaging" button.
     */
    fun onCreateClick(callsign: String)
}
