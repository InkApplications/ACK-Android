package com.inkapplications.aprs.data

import com.inkapplications.karps.client.AprsDataClient
import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.kotlin.filterEachNotNull
import com.inkapplications.kotlin.mapEach
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
            .map { PacketEntity(null, Instant.now().toEpochMilli(), it, PacketSource.Ax25) }
            .onEach { packetDao.addPacket(it) }
            .mapNotNull { tryParse(it) }
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
            .map { PacketEntity(null, Instant.now().toEpochMilli(), it.toByteArray(Charsets.UTF_8), PacketSource.AprsIs) }
            .onEach { packetDao.addPacket(it) }
            .mapNotNull { tryParse(it) }
            .onEach { mutableIncoming.emit(it) }
            .onEach { logger.debug("IS Packet Parsed: $it") }

    }

    override fun findRecent(count: Int): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .mapEach { tryParse(it) }
            .filterEachNotNull()
    }

    override fun findById(id: Long): Flow<CapturedPacket?> {
        return packetDao.findById(id).map { it?.let { tryParse(it) } }
    }

    private fun tryParse(packet: PacketEntity): CapturedPacket? {
        try {
            val parsed = when (packet.packetSource) {
                PacketSource.Ax25 -> parser.fromAx25(packet.data)
                PacketSource.AprsIs -> parser.fromString(packet.data.toString(Charsets.UTF_8))
            }
            return CapturedPacket(packet.id!!, packet.timestamp, parsed, packet.packetSource)
        } catch (error: Throwable) {
            logger.warn("Failed to parse packet: ${packet.id}")
            logger.debug { "Packet Data: ${packet.data.contentToString()} " }
            return null
        }
    }
}
