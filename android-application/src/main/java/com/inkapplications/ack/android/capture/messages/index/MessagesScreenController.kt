package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.structures.station.Callsign

/**
 * Actions that can be taken on the messages screen.
 */
interface MessagesScreenController {
    /**
     * Invoked when the user clicks the new conversation button.
     */
    fun onCreateMessageClick()

    /**
     * Invoked when the user clicks on a conversation
     *
     * @param callsign The callsign as an identifier for the conversation that was clicked.
     */
    fun onConversationClick(callsign: Callsign)
}
