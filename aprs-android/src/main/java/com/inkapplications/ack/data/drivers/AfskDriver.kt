package com.inkapplications.ack.data.drivers

import android.Manifest
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.*
import com.inkapplications.ack.data.AndroidAfskModulator
import com.inkapplications.ack.data.AudioDataProcessor
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.coroutines.combinePair
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class AfskDriver(
    private val aprsCodec: AprsCodec,
    private val packetStorage: PacketStorage,
    private val audioProcessor: AudioDataProcessor,
    private val modulator: AndroidAfskModulator,
    private val settings: DriverSettingsProvider,
    private val logger: KimchiLogger,
): PacketDriver {
    override val incoming = MutableSharedFlow<CapturedPacket>()
    override val receivePermissions: Set<String> = setOf(Manifest.permission.RECORD_AUDIO)
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
