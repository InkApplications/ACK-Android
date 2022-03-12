package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.toAddress
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.filterEach
import dagger.Reusable
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class MessageEvents @Inject constructor(
    packetStorage: PacketStorage,
    settings: SettingsReadAccess,
    connectionSettings: ConnectionSettings,
) {
    val screenState = packetStorage.findRecent(500)
        .filterEach { it.parsed.data is PacketData.Message }
        .combinePair(settings.observeString(connectionSettings.callsign))
        .map { (messages, callsign) -> messages.filter { (it.parsed.data as PacketData.Message).addressee.callsign.lowercase() == callsign.toAddress().callsign.lowercase() } }
        .map { if (it.isEmpty()) MessageScreenState.Empty else MessageScreenState.MessageList(it) }
}
