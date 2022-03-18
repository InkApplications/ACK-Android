package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.toAddress
import com.inkapplications.coroutines.filterEach
import dagger.Reusable
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class MessageEvents @Inject constructor(
    packetStorage: PacketStorage,
    settings: SettingsReadAccess,
    connectionSettings: ConnectionSettings,
) {
    val screenState = settings.observeString(connectionSettings.callsign)
        .map { it.toAddress() }
        .flatMapLatest { packetStorage.findByAddressee(it.callsign) }
        .filterEach { it.parsed.data is PacketData.Message }
        .map { if (it.isEmpty()) MessageScreenState.Empty else MessageScreenState.MessageList(it) }
}
