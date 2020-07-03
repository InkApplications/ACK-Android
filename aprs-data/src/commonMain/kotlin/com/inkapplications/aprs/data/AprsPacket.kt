package com.inkapplications.aprs.data

sealed class AprsPacket {
    abstract val source: Callsign?
    abstract val destination: Callsign?
    abstract val comment: String?

    class Location(
        override val source: Callsign?,
        override val destination: Callsign?,
        override val comment: String?,
        val position: Position
    ): AprsPacket()

    class Unknown(
        override val source: Callsign?,
        override val destination: Callsign?,
        override val comment: String?
    ): AprsPacket()
}

data class Position(
    val latitude: Double,
    val longitude: Double
)
