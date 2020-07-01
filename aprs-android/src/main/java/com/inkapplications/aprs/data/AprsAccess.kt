package com.inkapplications.aprs.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import net.ab0oo.aprs.parser.Parser

internal class AndroidAprs(
    audioProcessor: AudioDataProcessor
): AprsAccess {
    override val data: Flow<AprsPacket> = audioProcessor.data
        .map { Parser.parseAX25(it) }
        .filter { it.isAprs }
        .map { AprsPacketTransformer.fromParsed(it) }
}

