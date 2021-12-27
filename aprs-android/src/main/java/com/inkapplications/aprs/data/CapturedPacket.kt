package com.inkapplications.aprs.data

import com.inkapplications.karps.structures.AprsPacket

data class CapturedPacket(
    val id: Long,
    val received: Long,
    val parsed: AprsPacket,
    val source: PacketSource,
    val raw: ByteArray,
)
