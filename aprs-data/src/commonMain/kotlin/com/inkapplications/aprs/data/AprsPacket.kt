package com.inkapplications.aprs.data

sealed class AprsPacket {
    abstract val source: Callsign?
    abstract val destination: Callsign?
    abstract val comment: String?
    abstract val timestamp: Long

    data class Location(
        override val source: Callsign?,
        override val destination: Callsign?,
        override val comment: String?,
        override val timestamp: Long,
        val position: Position,
        val symbol: Pair<Char, Char>
    ): AprsPacket()

    data class Unknown(
        override val source: Callsign?,
        override val destination: Callsign?,
        override val comment: String?,
        override val timestamp: Long
    ): AprsPacket()
}

