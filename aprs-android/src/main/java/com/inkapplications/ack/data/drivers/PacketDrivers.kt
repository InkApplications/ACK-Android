package com.inkapplications.ack.data.drivers

import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig

class PacketDrivers(
    val internetDriver: InternetDriver,
    val afskDriver: AfskDriver,
) {
    suspend fun transmitAll(packet: AprsPacket, encodingConfig: EncodingConfig) {
        internetDriver.transmitPacket(packet, encodingConfig)
        afskDriver.transmitPacket(packet, encodingConfig)
    }
}
