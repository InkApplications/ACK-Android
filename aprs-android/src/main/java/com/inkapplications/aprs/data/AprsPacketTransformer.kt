package com.inkapplications.aprs.data

import net.ab0oo.aprs.parser.APRSPacket
import net.ab0oo.aprs.parser.PositionPacket

internal object AprsPacketTransformer {
    fun fromParsed(parsed: APRSPacket): AprsPacket {
        val source = parsed.sourceCall?.let(::Callsign)
        val destination = parsed.destinationCall?.let(::Callsign)
        val comment = parsed.aprsInformation?.comment

        return when (val info = parsed.aprsInformation) {
            is PositionPacket -> AprsPacket.Location(
                source = source,
                destination = destination,
                comment = comment,
                position = Position(info.position.latitude, info.position.longitude)
            )
            else -> AprsPacket.Unknown(
                source = parsed.sourceCall?.let(::Callsign),
                destination = parsed.destinationCall?.let(::Callsign),
                comment = parsed.aprsInformation?.comment
            )
        }
    }
}
