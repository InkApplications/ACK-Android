package com.inkapplications.aprs.data

import com.inkapplications.karps.client.AprsDataClient
import com.inkapplications.karps.client.Credentials
import com.inkapplications.karps.parser.AprsParser
import com.inkapplications.kotlin.filterEachNotNull
import com.inkapplications.kotlin.mapEach
import com.inkapplications.kotlin.startNull
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import org.threeten.bp.Instant
import kotlin.math.min

internal class AndroidAprs(
    audioProcessor: AudioDataProcessor,
    private val packetDao: PacketDao,
    private val client: AprsDataClient,
    private val locationProvider: AndroidLocationProvider,
    private val parser: AprsParser,
    private val logger: KimchiLogger
): AprsAccess {
    private val internetBuffer = MutableSharedFlow<CapturedPacket>(replay = 10000, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val incomingInternet: Flow<CapturedPacket> = locationProvider.location
        .flatMapLatest { coordinates ->
            flow {
                client.connect("rotate.aprs2.net", 14580, Credentials(""), listOf("r/${coordinates.latitude.asDecimal}/${coordinates.longitude.asDecimal}/100")) { receive, _ ->
                    receive.consumeEach { emit(it) }
                }
            }
        }
        .onEach { if (it.startsWith('#')) { logger.info("APRS-IS: $it") } }
        .filter { !it.startsWith('#') }
        .map { parser.fromString(it) }
        .map { CapturedPacket(Math.random().toLong(), Instant.now().toEpochMilli(), it) }
        .onEach { internetBuffer.emit(it) }
        .onEach { logger.debug("IS Packet Parsed: $it") }

    override val incomingAudio: Flow<CapturedPacket> = audioProcessor.data
        .map { PacketEntity(Instant.now().toEpochMilli(), it) }
        .onEach { packetDao.addPacket(it) }
        .mapNotNull { tryParse(it) }
        .onEach { logger.debug("APRS Packet Parsed: $it") }

    override fun findRecent(count: Int): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .mapEach { tryParse(it) }
            .filterEachNotNull()
            .let { combine(it, internetBuffer.startNull()) { a, b -> a + internetBuffer.replayCache } }
            .map { it.sortedByDescending { it.received }.let { it.subList(0, min(count, it.size)) } }
    }

    override fun findById(id: Long): Flow<CapturedPacket?> {
        return packetDao.findById(id).map { it?.let { tryParse(it) } }
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
