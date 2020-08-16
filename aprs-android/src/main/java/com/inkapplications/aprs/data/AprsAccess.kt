package com.inkapplications.aprs.data

import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.kotlin.filterEachNotNull
import com.inkapplications.kotlin.mapEach
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import org.threeten.bp.Instant

interface AprsAccess {
    val incoming: Flow<CapturedPacket>

    fun findRecent(count: Int): Flow<List<CapturedPacket>>
    fun findById(id: Long): Flow<CapturedPacket?>
}

internal class AndroidAprs(
    audioProcessor: AudioDataProcessor,
    private val packetDao: PacketDao,
    private val parser: AprsParser,
    private val logger: KimchiLogger
): AprsAccess {
    override val incoming: Flow<CapturedPacket> = audioProcessor.data
        .map { PacketEntity(Instant.now().toEpochMilli(), it) }
        .onEach { packetDao.addPacket(it) }
        .mapNotNull { tryParse(it) }
        .onEach { logger.debug("APRS Packet Parsed: $it") }

    override fun findRecent(count: Int): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .mapEach { tryParse(it) }
            .filterEachNotNull()
    }

    override fun findById(id: Long): Flow<CapturedPacket?> {
        return packetDao.findById(id).map { tryParse(it) }
    }

    private fun tryParse(packet: PacketEntity): CapturedPacket? {
        try {
            return CapturedPacket(packet.id!!, packet.timestamp, parser.fromAx25(packet.data))
        } catch (error: Throwable) {
            logger.warn("Failed to parse packet: ${packet.id}")
            logger.debug { "Packet Data: ${packet.data.contentToString()} " }
            return null
        }
    }
}

