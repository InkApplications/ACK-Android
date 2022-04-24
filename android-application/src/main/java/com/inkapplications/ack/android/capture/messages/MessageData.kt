package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.station.Callsign

data class MessageData(
    val selfCallsign: Callsign,
    val message: CapturedPacket,
)
