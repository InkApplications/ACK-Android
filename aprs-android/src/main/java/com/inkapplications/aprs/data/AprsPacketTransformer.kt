package com.inkapplications.aprs.data

import net.ab0oo.aprs.parser.APRSPacket

internal object AprsPacketTransformer {
    fun fromParsed(parsed: APRSPacket): AprsPacket {
        return AprsPacket(
            source = parsed.sourceCall?.let(::Callsign),
            destination = parsed.destinationCall?.let(::Callsign),
            comment = parsed.aprsInformation?.comment
        )
    }
}
