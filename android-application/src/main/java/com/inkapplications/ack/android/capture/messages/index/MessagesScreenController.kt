package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.structures.station.Callsign

interface MessagesScreenController {
    fun onCreateMessageClick()
    fun onConversationClick(callsign: Callsign)
}
