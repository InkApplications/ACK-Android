package com.inkapplications.aprs.data

import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Instant

internal class AndroidAprs(
    audioProcessor: AudioDataProcessor,
    private val packetDao: PacketDao,
    logger: KimchiLogger
): AprsAccess {
    override val data: Flow<AprsPacket> = audioProcessor.data
        .map { PacketEntity(Instant.now().toEpochMilli(), it) }
        .onEach { packetDao.addPacket(it) }
        .map { AprsPacketTransformer.fromParsed(it) }
        .onEach { logger.debug("APRS Packet Parsed: $it") }

    override fun findRecent(count: Int): Flow<List<AprsPacket>> {
        return packetDao.findRecent(count)
            .map { it.map { AprsPacketTransformer.fromParsed(it) } }
    }
}

