package com.inkapplications.aprs.data

data class AprsPacket(
    val source: Callsign?,
    val destination: Callsign?,
    val comment: String?
)
