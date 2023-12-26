package com.inkapplications.ack.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.coroutines.filterItemNotNull
import com.inkapplications.coroutines.mapItems
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.reflect.KClass

internal class DaoPacketStorage(
    private val packetDao: CapturedPacketEntityQueries,
    private val codec: AprsCodec,
    private val clock: Clock,
    private val logger: KimchiLogger,
): PacketStorage {
    private fun <T: Any> Query<T>.toListFlow() = asFlow().mapToList(Dispatchers.IO)
    private fun <T: Any> Query<T>.toSingleFlow() = asFlow().mapToOne(Dispatchers.IO)
    private fun <T: Any> Query<T>.toMaybeFlow() = asFlow().mapToOneOrNull(Dispatchers.IO)

    override fun findRecent(count: Long): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .toListFlow()
            .map { entities ->
                entities.mapNotNull { createCapturedPacket(it, fromEntityOrNull(it)) }
            }
    }

    override fun findById(id: CaptureId): Flow<CapturedPacket?> {
        return packetDao.findById(id)
            .toMaybeFlow()
            .map { it?.let { createCapturedPacket(it, fromEntityOrNull(it)) } }
    }

    override fun findLatestByConversation(callsign: Callsign): Flow<List<CapturedPacket>> {
        return packetDao.findLatestConversationMessages(callsign)
            .toListFlow()
            .mapItems { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterItemNotNull()
    }

    override fun findConversation(addressee: Callsign, callsign: Callsign): Flow<List<CapturedPacket>> {
        return packetDao.findConversation(addressee, callsign)
            .toListFlow()
            .mapItems { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterItemNotNull()
    }

    override fun count(): Flow<Long> {
        return packetDao.countAll().toSingleFlow()
    }

    override fun countStations(): Flow<Long> {
        return packetDao.countSources().toSingleFlow()
    }

    override fun findByStationComments(limit: Long?): Flow<List<CapturedPacket>> {
        return packetDao.findStationComments(limit ?: -1L)
            .toListFlow()
            .mapItems { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterItemNotNull()
    }

    override fun findMostRecentByType(type: KClass<out PacketData>): Flow<CapturedPacket?> {
        return packetDao.findMostRecentByType(type.simpleName!!)
            .toMaybeFlow()
            .map { if (it == null) null else createCapturedPacket(it, fromEntityOrNull(it)) }
    }

    override fun findBySource(callsign: Callsign, limit: Long?): Flow<List<CapturedPacket>> {
        return packetDao.findBySourceCallsign(callsign, limit ?: -1)
            .toListFlow()
            .mapItems { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterItemNotNull()
    }

    override suspend fun save(data: ByteArray, packet: AprsPacket, origin: PacketOrigin): CapturedPacket {
        val now = clock.now()
        return packetDao.transactionWithResult {
            packetDao.addPacket(
                timestamp = now,
                raw_data = data,
                packet_origin = origin,
                source_callsign = packet.route.source.callsign,
                addressee_callsign = (packet.data as? PacketData.Message)?.addressee?.callsign,
                data_type = packet.data.javaClass.simpleName,
                comment_field = packet.data.commentLikeField,
            )

            packetDao.lastId()
        }.executeAsOne().let {
            CapturedPacket(
                id = CaptureId(it),
                received = now,
                parsed = packet,
                origin = origin,
                raw = data,
            )
        }
    }

    private fun fromEntityOrNull(data: CapturedPacketEntity): AprsPacket? {
        try {
            return when (data.packet_origin) {
                PacketOrigin.Ax25, PacketOrigin.Tnc -> codec.fromAx25(data.raw_data)
                PacketOrigin.AprsIs, PacketOrigin.Local -> codec.fromString(data.raw_data.toString(Charsets.UTF_8))
            }
        } catch (error: Throwable) {
            logger.warn("Unable to parse packet", error)
            return null
        }
    }

    private fun createCapturedPacket(entity: CapturedPacketEntity, parsed: AprsPacket?): CapturedPacket? {
        parsed ?: return null

        return CapturedPacket(
            id = entity.id,
            received = entity.timestamp,
            parsed = parsed,
            origin = entity.packet_origin,
            raw = entity.raw_data,
        )
    }
}
