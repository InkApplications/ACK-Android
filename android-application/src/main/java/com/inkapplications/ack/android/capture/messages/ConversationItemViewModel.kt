package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.structures.station.Callsign

data class ConversationItemViewModel(
    val name: String,
    val messagePreview: String,
    val idCallsign: Callsign,
)
