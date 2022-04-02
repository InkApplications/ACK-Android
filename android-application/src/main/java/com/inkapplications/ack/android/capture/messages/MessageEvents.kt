package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.capture.messages.conversation.ConverstationViewState
import com.inkapplications.ack.android.capture.messages.conversation.MessageItemViewModel
import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewModel
import com.inkapplications.ack.android.capture.messages.index.MessageIndexScreenState
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.station.toStationAddress
import com.inkapplications.android.extensions.ViewModelFactory
import com.inkapplications.coroutines.filterEach
import com.inkapplications.coroutines.mapEach
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Reusable
class MessageEvents @Inject constructor(
    private val packetStorage: PacketStorage,
    private val settings: SettingsReadAccess,
    private val connectionSettings: ConnectionSettings,
    private val transmitSettings: TransmitSettings,
    private val messageItemFactory: ViewModelFactory<CapturedPacket, MessageItemViewModel>,
    private val conversationItemFactory: ViewModelFactory<Pair<Callsign, List<CapturedPacket>>, ConversationItemViewModel>,
    private val codec: AprsCodec,
    private val drivers: PacketDrivers,
    private val logger: KimchiLogger,
) {
    val messagesScreenState = settings.observeData(connectionSettings.address)
        .onEach { logger.debug("Observing conversations for addressee: $it") }
        .flatMapLatest { (it?.callsign?.let(packetStorage::findByAddressee) ?: flowOf(emptyList())) }
        .onEach { logger.debug("Found ${it.size} conversations") }
        .filterEach { it.parsed.data is PacketData.Message }
        .map { it.groupBy { it.parsed.route.source.callsign } }
        .map {
            it.entries.map { it.toPair() }.map(conversationItemFactory::create)
        }
        .map { if (it.isEmpty()) MessageIndexScreenState.Empty else MessageIndexScreenState.ConversationList(it) }

    fun conversationViewState(address: Callsign): Flow<ConverstationViewState> {
        return settings.observeData(connectionSettings.address)
            .flatMapLatest { (it?.callsign?.let(packetStorage::findByAddressee) ?: flowOf(emptyList())) }
            .filterEach { it.parsed.route.source.callsign == address }
            .filterEach { it.parsed.data is PacketData.Message }
            .onEach { logger.debug("Loaded ${it.size} messages from $address") }
            .mapEach(messageItemFactory::create)
            .map { ConverstationViewState.MessageList(address.canonical, it) }
    }

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
