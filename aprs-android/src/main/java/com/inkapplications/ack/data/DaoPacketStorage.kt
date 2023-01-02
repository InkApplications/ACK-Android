package com.inkapplications.ack.data

import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.coroutines.filterEachNotNull
import com.inkapplications.coroutines.mapEach
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.reflect.KClass

internal class DaoPacketStorage(
    private val packetDao: PacketDao,
    private val codec: AprsCodec,
    private val clock: Clock,
    private val logger: KimchiLogger,
): PacketStorage {
    override fun findRecent(count: Int): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .map { entities ->
                entities.mapNotNull { createCapturedPacket(it, fromEntityOrNull(it)) }
            }
            .filterEachNotNull()
    }

    override fun findById(id: Long): Flow<CapturedPacket?> {
        return packetDao.findById(id).map { it?.let { createCapturedPacket(it, fromEntityOrNull(it)) } }
    }

    override fun findLatestByConversation(callsign: Callsign): Flow<List<CapturedPacket>> {
        return packetDao.findLatestConversationMessages(callsign.canonical)
            .mapEach { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterEachNotNull()
    }

    override fun findConversation(addressee: Callsign, callsign: Callsign): Flow<List<CapturedPacket>> {
        return packetDao.findConversation(addressee.canonical, callsign.canonical)
            .mapEach { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterEachNotNull()
    }

    override fun count(): Flow<Int> {
        return packetDao.countAll()
    }

    override fun countStations(): Flow<Int> {
        return packetDao.countSources()
    }

    override fun findMostRecentByType(type: KClass<out PacketData>): Flow<CapturedPacket?> {
        return packetDao.findMostRecentByType(type.simpleName!!)
            .map { if (it == null) null else createCapturedPacket(it, fromEntityOrNull(it)) }
    }

    override fun findBySource(callsign: Callsign, limit: Int?): Flow<List<CapturedPacket>> {
        return packetDao.findBySourceCallsign(callsign.canonical, limit ?: -1)
            .mapEach { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterEachNotNull()
    }

    override suspend fun save(data: ByteArray, packet: AprsPacket, source: PacketSource): CapturedPacket {
        val entity = PacketEntity(
            id = null,
            timestamp = clock.now().toEpochMilliseconds(),
            data = data,
            packetSource = source,
            sourceCallsign = packet.route.source.callsign.canonical,
            addresseeCallsign = (packet.data as? PacketData.Message)?.addressee?.callsign?.canonical,
            dataType = packet.data.javaClass.simpleName,
        )
        val id = packetDao.addPacket(entity)

        return CapturedPacket(
            id = id,
            received = Instant.fromEpochMilliseconds(entity.timestamp),
            parsed = packet,
            source = entity.packetSource,
            raw = entity.data,
        )
    }

    private fun fromEntityOrNull(data: PacketEntity): AprsPacket? {
        try {
            return when (data.packetSource) {
                PacketSource.Ax25 -> codec.fromAx25(data.data)
                PacketSource.AprsIs, PacketSource.Local -> codec.fromString(data.data.toString(Charsets.UTF_8))
            }
        } catch (error: Throwable) {
            logger.warn("Unable to parse packet", error)
            return null
        }
    }

    private fun createCapturedPacket(entity: PacketEntity, parsed: AprsPacket?, id: Long = entity.id!!): CapturedPacket? {
        parsed ?: return null

        return CapturedPacket(
            id = id,
            received = Instant.fromEpochMilliseconds(entity.timestamp),
            parsed = parsed,
            source = entity.packetSource,
            raw = entity.data,
        )
    }
}
