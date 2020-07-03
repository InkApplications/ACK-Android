package com.inkapplications.aprs.data

import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import net.ab0oo.aprs.parser.Parser

internal class AndroidAprs(
    audioProcessor: AudioDataProcessor,
    logger: KimchiLogger
): AprsAccess {
    override val data: Flow<AprsPacket> = audioProcessor.data
        .map { Parser.parseAX25(it) }
        .filter { it.isAprs }
        .map { AprsPacketTransformer.fromParsed(it) }
        .onEach { logger.debug("APRS Packet Parsed: $it") }
}

