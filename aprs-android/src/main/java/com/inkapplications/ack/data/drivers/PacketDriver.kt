package com.inkapplications.ack.data.drivers

import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import kotlinx.coroutines.flow.Flow

interface PacketDriver {
    val connectionState: Flow<DriverConnectionState>
    val incoming: Flow<CapturedPacket>
    val transmitPermissions get() = emptySet<String>()
    val receivePermissions get() = emptySet<String>()
    suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig)
    suspend fun connect()
    suspend fun disconnect()
}
