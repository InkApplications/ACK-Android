package com.inkapplications.ack.data

import com.inkapplications.ack.structures.AprsPacket

data class CapturedPacket(
    val id: Long,
    val received: Long,
    val parsed: AprsPacket,
    val source: PacketSource,
    val raw: ByteArray,
)
