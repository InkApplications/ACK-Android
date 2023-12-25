package com.inkapplications.ack.android

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.PacketRoute
import com.inkapplications.ack.structures.station.StationAddress
import kotlinx.datetime.Instant

val testRoute = PacketRoute(
    source = StationAddress("KE0YOG"),
    destination = StationAddress("KE0YOG", "1"),
    digipeaters = listOf(),
)

fun AprsPacket.toTestCapturedPacket() = CapturedPacket(
    id = 1,
    received = Instant.fromEpochMilliseconds(0),
    parsed = this,
    source = PacketSource.AprsIs,
    raw = byteArrayOf(),
)

fun PacketData.toTestPacket() = AprsPacket(
    route = testRoute,
    data = this,
)
