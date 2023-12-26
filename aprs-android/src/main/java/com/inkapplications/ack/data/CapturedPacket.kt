package com.inkapplications.ack.data

import com.inkapplications.ack.structures.AprsPacket
import kotlinx.datetime.Instant

data class CapturedPacket(
    val id: CaptureId,
    val received: Instant,
    val parsed: AprsPacket,
    val source: PacketSource,
    val raw: ByteArray,
)
