package com.inkapplications.aprs.android

import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.aprs.data.PacketSource
import com.inkapplications.karps.structures.*

val testRoute = PacketRoute(
    source = Address("KE0YOG"),
    destination = Address("KE0YOG", "1"),
    digipeaters = listOf(),
)

fun AprsPacket.toTestCapturedPacket() = CapturedPacket(
    id = 1,
    received = 0,
    parsed = this,
    source = PacketSource.AprsIs,
    raw = byteArrayOf(),
)

fun PacketData.toTestPacket() = AprsPacket(
    route = testRoute,
    data = this,
)
