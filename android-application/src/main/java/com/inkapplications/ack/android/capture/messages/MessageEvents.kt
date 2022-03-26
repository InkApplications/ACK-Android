package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
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
    private val viewFactory: MessageViewModelFactory,
    private val logger: KimchiLogger,
) {
    val messagesScreenState = settings.observeData(connectionSettings.address)
        .onEach { logger.debug("Observing conversations for addressee: $it") }
        .flatMapLatest { (it?.callsign?.let(packetStorage::findByAddressee) ?: flowOf(emptyList())) }
        .onEach { logger.debug("Found ${it.size} conversations") }
        .filterEach { it.parsed.data is PacketData.Message }
        .map { it.groupBy { it.parsed.route.source.callsign } }
        .map {
            it.entries.map { (callsign, messages) ->
                ConversationViewModel(callsign.canonical, (messages.last().parsed.data as PacketData.Message).message, callsign)
            }
        }
        .map { if (it.isEmpty()) MessageScreenState.Empty else MessageScreenState.ConversationList(it) }

    fun conversationViewState(address: Callsign): Flow<ConverstationViewState> {
        return settings.observeData(connectionSettings.address)
            .flatMapLatest { (it?.callsign?.let(packetStorage::findByAddressee) ?: flowOf(emptyList())) }
            .filterEach { it.parsed.route.source.callsign == address }
            .filterEach { it.parsed.data is PacketData.Message }
            .onEach { logger.debug("Loaded ${it.size} messages from $address") }
            .mapEach(viewFactory::createMessageItem)
            .map { ConverstationViewState.MessageList(address.canonical, it) }
    }
}
