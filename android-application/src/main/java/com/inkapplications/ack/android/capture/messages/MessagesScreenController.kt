package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.structures.station.Callsign

interface MessagesScreenController {
    fun onCreateMessageClick()
    fun onConversationClick(callsign: Callsign)
}
