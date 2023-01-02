package com.inkapplications.ack.data.drivers

import android.Manifest
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.*
import com.inkapplications.ack.data.AndroidAfskModulator
import com.inkapplications.ack.data.AudioDataProcessor
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.coroutines.combinePair
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.structure.roundToInt
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AfskDriver internal constructor(
    private val aprsCodec: AprsCodec,
    private val packetStorage: PacketStorage,
    private val audioProcessor: AudioDataProcessor,
    private val modulator: AndroidAfskModulator,
    private val settings: DriverSettingsProvider,
    private val logger: KimchiLogger,
): PacketDriver, AudioConnectionMonitor {
    override val incoming = MutableSharedFlow<CapturedPacket>()
    override val receivePermissions: Set<String> = setOf(Manifest.permission.RECORD_AUDIO)
    override val volume = audioProcessor.volume
    private val transmitQueue = MutableSharedFlow<ByteArray>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig) {
        val data = try {
            aprsCodec.toAx25(packet, encodingConfig)
        } catch (encodingError: Throwable) {
            logger.error("Unable to encode packet for AX25", encodingError)
            return
        }

        transmitQueue.emit(data)
    }

    override suspend fun connect() {
        logger.debug("Starting Audio Packet Capture")

        coroutineScope {
            launch {
                audioProcessor.data
                    .mapNotNull { captureAx25Packet(it) }
                    .onEach { logger.debug("APRS Packet Parsed: $it") }
                    .collect { incoming.emit(it) }
            }
            launch {
                settings.afskConfiguration.combinePair(transmitQueue)
                    .collect { (config, data) ->
                        logger.info("Modulating ${data.size} bytes with a ${config.preamble} preamble at ${config.volume.toWholePercentage().roundToInt()}% volume")
                        modulator.modulate(data, config.preamble, config.volume)
                    }
            }
        }
    }

    private suspend fun captureAx25Packet(data: ByteArray): CapturedPacket? {
        val parsed = try {
            aprsCodec.fromAx25(data)
        } catch (parsingError: Throwable) {
            logger.warn("Unable to parse packet", parsingError)
            return null
        }
        return packetStorage.save(data, parsed, PacketSource.Ax25)
    }
}
