package com.inkapplications.aprs.data

import net.ab0oo.aprs.parser.Parser
import net.ab0oo.aprs.parser.PositionPacket

internal object AprsPacketTransformer {
    fun fromParsed(entity: PacketEntity): AprsPacket {
        val parsed = Parser.parseAX25(entity.data)
        val source = parsed.sourceCall?.let(::Callsign)
        val destination = parsed.destinationCall?.let(::Callsign)
        val comment = parsed.aprsInformation?.comment

        return when (val info = parsed.aprsInformation) {
            is PositionPacket -> AprsPacket.Location(
                source = source,
                destination = destination,
                comment = comment,
                timestamp = entity.timestamp,
                position = Position(info.position.latitude, info.position.longitude)
            )
            else -> AprsPacket.Unknown(
                source = parsed.sourceCall?.let(::Callsign),
                destination = parsed.destinationCall?.let(::Callsign),
                comment = parsed.aprsInformation?.comment,
                timestamp = entity.timestamp
            )
        }
    }
}
