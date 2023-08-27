package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.station.toStationAddress
import com.inkapplications.coroutines.filterItems
import com.inkapplications.coroutines.mapItems
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Aggregates events and settings for packet data relating to messages.
 */
@Reusable
class MessageEvents @Inject constructor(
    private val packetStorage: PacketStorage,
    private val settings: SettingsReadAccess,
    private val connectionSettings: ConnectionSettings,
    private val transmitSettings: TransmitSettings,
    private val codec: AprsCodec,
    private val drivers: PacketDrivers,
    private val logger: KimchiLogger,
) {
    /**
     * Observe a list of the latest message in each distinct conversation
     * by station callsign.
     */
    val latestMessageByConversation = settings.observeData(connectionSettings.address)
        .onEach { logger.debug("Observing conversations for addressee: $it") }
        .flatMapLatest { address ->
            address?.callsign?.let(packetStorage::findLatestByConversation)
                ?.mapItems { MessageData(address.callsign, it) }
                ?: flowOf(emptyList())
        }
        .onEach { logger.debug("Found ${it.size} conversations") }

    /**
     * Observe the total list of messages sent to and from a particular station.
     *
     * @param address The callsign of the station to find messages for.
     */
    fun conversationList(address: Callsign): Flow<List<MessageData>> {
        return settings.observeData(connectionSettings.address)
            .flatMapLatest { self ->
                (self?.callsign?.let { packetStorage.findConversation(address, it) }?.mapItems { MessageData(self.callsign, it) } ?: flowOf(emptyList()))
            }
            .filterItems { it.message.parsed.data is PacketData.Message }
            .onEach { logger.debug("Loaded ${it.size} messages from $address") }
    }

    /**
     * Transmit a new message via all connected transmission drivers.
     *
     * This will immediately transmit the message to any connected drivers
     * as well as save the message in the local database as a locally
     * transmitted packet.
     *
     * @param addressee The callsign of the station to send the message to.
     * @param message The body text of the message to be sent.
     */
    suspend fun transmitMessage(addressee: Callsign, message: String) {
        val packetData = PacketData.Message(
            addressee = StationAddress(addressee),
            message = message,
        )

        val address = settings.observeData(connectionSettings.address).first() ?: run {
            logger.error("Cannot transmit without a callsign set!")
            return
        }
        val path = settings.observeString(transmitSettings.digipath).first()
        val destination = settings.observeString(transmitSettings.destination).first()

        val packet = AprsPacket(
            route = PacketRoute(
                source = address,
                digipeaters = path.split(',').map { Digipeater(it.toStationAddress()) },
                destination = destination.toStationAddress(),
            ),
            data = packetData,
        )
        val encodingConfig = EncodingConfig(compression = EncodingPreference.Disfavored)

        drivers.transmitAll(packet, encodingConfig)
        val encoded = codec.toString(packet)
        packetStorage.save(encoded.toByteArray(), packet, PacketSource.Local)
    }
}
