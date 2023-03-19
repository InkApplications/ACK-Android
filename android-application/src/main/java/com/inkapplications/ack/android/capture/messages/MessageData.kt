package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.station.Callsign

/**
 * Contextual information used for displaying messages.
 */
data class MessageData(
    /**
     * Callsign of the user of the app. Used to distinguished "my" messages.
     */
    val selfCallsign: Callsign,

    /**
     * Packet data for the message.
     */
    val message: CapturedPacket,
) {
    /**
     * Whether the sender matches the users callsign.
     */
    val isOutgoing = message.parsed.route.source.callsign == selfCallsign
}
