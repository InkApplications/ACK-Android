package com.inkapplications.aprs.data

import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.karps.structures.AprsPacket
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import org.threeten.bp.Instant

interface AprsAccess {
    val data: Flow<AprsPacket>

    fun findRecent(count: Int): Flow<List<AprsPacket>>
}

internal class AndroidAprs(
    audioProcessor: AudioDataProcessor,
    private val packetDao: PacketDao,
    private val parser: AprsParser,
    private val logger: KimchiLogger
): AprsAccess {
    override val data: Flow<AprsPacket> = audioProcessor.data
        .map { PacketEntity(Instant.now().toEpochMilli(), it) }
        .onEach { packetDao.addPacket(it) }
        .mapNotNull { tryParse(it) }
        .onEach { logger.debug("APRS Packet Parsed: $it") }

    override fun findRecent(count: Int): Flow<List<AprsPacket>> {
        return packetDao.findRecent(count)
            .map { it.mapNotNull { tryParse(it) } }
    }

    private fun tryParse(packet: PacketEntity): AprsPacket? {
        try {
            return parser.fromAx25(packet.data)
        } catch (error: Throwable) {
            logger.warn("Failed to parse packet: ${packet.id}")
            logger.debug { "Packet Data: ${packet.data.contentToString()} " }
            return null
        }
    }
}

