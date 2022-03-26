package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ViewModelFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Create the viewmodel for items in a list of Message conversations.
 */
@Reusable
class ConversationItemViewModelFactory @Inject constructor(): ViewModelFactory<Pair<Callsign, List<CapturedPacket>>, ConversationItemViewModel> {
    override fun create(data: Pair<Callsign, List<CapturedPacket>>): ConversationItemViewModel {
        val (callsign, messages) = data

        return ConversationItemViewModel(
            name = callsign.canonical,
            messagePreview = (messages.last().parsed.data as PacketData.Message).message,
            idCallsign = callsign,
        )
    }
}
