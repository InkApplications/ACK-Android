package com.inkapplications.ack.data.drivers

import android.Manifest
import android.os.Build
import com.inkapplications.ack.client.AprsDataClient
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.*
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.coroutines.combinePair
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.value
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlin.math.pow

class InternetDriver internal constructor(
    private val aprsCodec: AprsCodec,
    private val packetStorage: PacketStorage,
    private val client: AprsDataClient,
    private val locationProvider: AndroidLocationProvider,
    private val settings: DriverSettingsProvider,
    private val runScope: CoroutineScope,
    private val logger: KimchiLogger,
): PacketDriver {
    private val runJob = MutableStateFlow<Job?>(null)
    private val clientConnectionState = MutableStateFlow(DriverConnectionState.Disconnected)
    override val connectionState: Flow<DriverConnectionState> = clientConnectionState
    override val incoming = MutableSharedFlow<CapturedPacket>()
    override val receivePermissions: Set<String> =  when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> setOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION)
        else -> setOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    override val transmitPermissions: Set<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> setOf(Manifest.permission.POST_NOTIFICATIONS)
        else -> emptySet()
    }
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
        logger.debug("Connecting Internet Packet Capture")

        runJob.updateAndGet { priorJob ->
            priorJob?.takeIf { it.isActive } ?: launchNewJob()
        }
    }

    override suspend fun disconnect() {
        clientConnectionState.value = DriverConnectionState.Disconnecting
        runJob.value?.cancelAndJoin()
    }

    private fun launchNewJob(): Job {
        logger.debug("Launching new job for Internet Packet Capture")
        return runScope.launch {
            settings.internetServiceConfiguration
                .combinePair(locationProvider.location)
                .onEach { logger.debug("New Settings/Location pair: $it") }
                .flatMapLatest { (settings, location) -> connectWithRetry(settings, location) }
                .onEach { if (it.startsWith('#')) logger.info("APRS-IS: $it") }
                .filter { !it.startsWith('#') }
                .mapNotNull { captureStringPacket(it) }
                .onEach { logger.debug("IS Packet Parsed: $it") }
                .flowOn(Dispatchers.IO)
                .collect { incoming.emit(it) }
        }
    }

    private fun connectWithRetry(settings: ConnectionConfiguration, location: GeoCoordinates): Flow<String> {
        return callbackFlow {
            logger.info("Opening APRS-IS Client to ${settings.host}:${settings.port}")
            var attempts = 0
            while (currentCoroutineContext().isActive) {
                clientConnectionState.value = DriverConnectionState.Connecting
                try {
                    client.connect(
                        server = settings.host,
                        port = settings.port,
                        credentials = settings.credentials,
                        filters = listOf(
                            "r/${location.latitude.asDecimal}/${location.longitude.asDecimal}/${
                                settings.searchRadius.toMeters().value(Kilo).toInt()
                            }"
                        )
                    ) { read, write ->
                        logger.info("APRS-IS Connected")
                        clientConnectionState.value = DriverConnectionState.Connected
                        attempts = 0
                        coroutineScope {
                            launch { read.consumeEach { send(it) } }
                            launch { transmitQueue.collect { write.send(it) } }
                        }
                    }
                } catch (error: CancellationException) {
                    logger.trace("APRS-IS Connection cancelling", error)
                    throw error
                } catch (error: Throwable) {
                    logger.error("APRS-IS Connection terminated unexpectedly", error)
                    delay(100*(2.0.pow(attempts)).toLong().coerceAtMost(60_000))
                    ++attempts
                } finally {
                    clientConnectionState.value = DriverConnectionState.Disconnected
                }
            }
        }
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
