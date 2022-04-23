package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.structures.station.Callsign

data class ConversationItemViewModel(
    val name: String,
    val messagePreview: String,
    val correspondent: Callsign,
)
