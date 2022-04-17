package com.inkapplications.ack.data.drivers

import android.Manifest
import com.inkapplications.ack.client.AprsDataClient
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.*
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.coroutines.combinePair
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.value
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InternetDriver internal constructor(
    private val aprsCodec: AprsCodec,
    private val packetStorage: PacketStorage,
    private val client: AprsDataClient,
    private val locationProvider: AndroidLocationProvider,
    private val settings: DriverSettingsProvider,
    private val logger: KimchiLogger,
): PacketDriver {
    override val incoming = MutableSharedFlow<CapturedPacket>()
    override val receivePermissions: Set<String> = setOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private val transmitQueue = MutableSharedFlow<String>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig) {
        val encoded = try {
            aprsCodec.toString(packet, encodingConfig)
        } catch (encodingError: Throwable) {
            logger.error("Unable to encode packet to String", encodingError)
            return
        }

        transmitQueue.emit(encoded)
    }

    override suspend fun connect() {
        settings.internetServiceConfiguration
            .combinePair(locationProvider.location)
            .onEach { logger.debug("New Settings/Location pair: $it") }
            .flatMapLatest { (settings, location) ->
                callbackFlow {
                    logger.info("Opening APRS-IS Client to ${settings.host}:${settings.port}")
                    client.connect(
                        server = settings.host,
                        port = settings.port,
                        credentials = settings.credentials,
                        filters = listOf(
                            "r/${location.latitude.asDecimal}/${location.longitude.asDecimal}/${
                                settings.searchRadius.value(
                                    Kilo,
                                    Meters
                                ).toInt()
                            }"
                        )
                    ) { read, write ->
                        coroutineScope {
                            launch { read.consumeEach { send(it) } }
                            launch { transmitQueue.collect { write.send(it) } }
                        }
                    }
                }
            }
            .onEach { if (it.startsWith('#')) logger.info("APRS-IS: $it") }
            .filter { !it.startsWith('#') }
            .mapNotNull { captureStringPacket(it) }
            .onEach { logger.debug("IS Packet Parsed: $it") }
            .flowOn(Dispatchers.IO)
            .collect { incoming.emit(it) }
    }

    private suspend fun captureStringPacket(data: String): CapturedPacket? {
        val parsed = try {
            aprsCodec.fromString(data)
        } catch (parsingError: Throwable) {
            logger.warn("Unable to parse packet", parsingError)
            return null
        }

        return packetStorage.save(data.toByteArray(Charsets.US_ASCII), parsed, PacketSource.AprsIs)
    }
}
