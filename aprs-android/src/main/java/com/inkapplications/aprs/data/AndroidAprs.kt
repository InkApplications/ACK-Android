package com.inkapplications.aprs.data

import com.inkapplications.karps.client.AprsDataClient
import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.kotlin.filterEachNotNull
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.value
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import org.threeten.bp.Instant

internal class AndroidAprs(
    private val audioProcessor: AudioDataProcessor,
    private val packetDao: PacketDao,
    private val client: AprsDataClient,
    private val locationProvider: AndroidLocationProvider,
    private val parser: AprsParser,
    private val logger: KimchiLogger
): AprsAccess {
    private val mutableIncoming = MutableSharedFlow<CapturedPacket>()
    override val incoming: Flow<CapturedPacket> = mutableIncoming

    override fun listenForAudioPackets(): Flow<CapturedPacket> {
        logger.debug("Starting Audio Packet Capture")

        return audioProcessor.data
            .mapNotNull { captureAx25Packet(it) }
            .onEach { mutableIncoming.emit(it) }
            .onEach { logger.debug("APRS Packet Parsed: $it") }
    }

    override fun listenForInternetPackets(settings: ConnectionConfiguration): Flow<CapturedPacket> {
        logger.debug("Opening APRS-IS Client to ${settings.host}:${settings.port}")

        return locationProvider.location
            .flatMapLatest { coordinates ->
                flow {
                    client.connect(
                        server = settings.host,
                        port = settings.port,
                        credentials = settings.credentials,
                        filters = listOf("r/${coordinates.latitude.asDecimal}/${coordinates.longitude.asDecimal}/${settings.searchRadius.value(Kilo, Meters).toInt()}")
                    ) { receive, _ ->
                        receive.consumeEach { emit(it) }
                    }
                }
            }
            .onEach { if (it.startsWith('#')) { logger.info("APRS-IS: $it") } }
            .filter { !it.startsWith('#') }
            .mapNotNull { captureStringPacket(it) }
            .onEach { mutableIncoming.emit(it) }
            .onEach { logger.debug("IS Packet Parsed: $it") }

    }

    override fun findRecent(count: Int): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .map { entities ->
                entities.mapNotNull { createCapturedPacket(it, parser.fromEntityOrNull(it)) }
            }
            .filterEachNotNull()
    }

    override fun findById(id: Long): Flow<CapturedPacket?> {
        return packetDao.findById(id).map { it?.let { createCapturedPacket(it, parser.fromEntityOrNull(it)) } }
    }

    private suspend fun captureAx25Packet(data: ByteArray): CapturedPacket? {
        val parsed = parser.fromAx25OrNull(data) ?: return null
        val entity = PacketEntity(null, Instant.now().toEpochMilli(), data, PacketSource.Ax25, parsed.route.source.callsign)
        val id = packetDao.addPacket(entity)

        return createCapturedPacket(entity, parsed, id)
    }

    private suspend fun captureStringPacket(data: String): CapturedPacket? {
        val parsed = parser.fromStringOrNull(data) ?: return null
        val entity = PacketEntity(null, Instant.now().toEpochMilli(), data.toByteArray(Charsets.UTF_8), PacketSource.AprsIs, parsed.route.source.callsign)
        val id = packetDao.addPacket(entity)

        return createCapturedPacket(entity, parsed, id)
    }

    private fun createCapturedPacket(entity: PacketEntity, parsed: AprsPacket?, id: Long = entity.id!!): CapturedPacket? {
        parsed ?: return null

        return CapturedPacket(
            id = id,
            received = entity.timestamp,
            parsed = parsed,
            source = entity.packetSource,
            raw = entity.data,
        )
    }

    private fun AprsParser.fromEntityOrNull(data: PacketEntity): AprsPacket? {
        return when (data.packetSource) {
            PacketSource.Ax25 -> fromAx25OrNull(data.data)
            PacketSource.AprsIs -> fromStringOrNull(data.data.toString(Charsets.UTF_8))
        }
    }

    private fun AprsParser.fromAx25OrNull(data: ByteArray): AprsPacket? {
        try {
            return fromAx25(data)
        } catch (error: Throwable) {
            logger.warn("Failed to parse packet")
            logger.debug { "Packet Data: ${data.contentToString()} " }
            return null
        }
    }

    private fun AprsParser.fromStringOrNull(data: String): AprsPacket? {
        try {
            return fromString(data)
        } catch (error: Throwable) {
            logger.warn("Failed to parse packet")
            logger.debug { "Packet Data: $data " }
            return null
        }
    }
}
