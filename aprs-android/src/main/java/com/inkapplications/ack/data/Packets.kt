package com.inkapplications.ack.data

import com.inkapplications.ack.structures.PacketData

/**
 * Extract any comment-like field from a packet, if available.
 */
val PacketData.commentLikeField: String? get() = when(this) {
    is PacketData.ItemReport -> comment
    is PacketData.Message -> message
    is PacketData.ObjectReport -> comment
    is PacketData.Position -> comment
    is PacketData.StatusReport -> status
    is PacketData.TelemetryReport -> comment
    is PacketData.CapabilityReport,
    is PacketData.Unknown,
    is PacketData.Weather -> null
}
