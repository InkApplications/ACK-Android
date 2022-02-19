package com.inkapplications.ack.data

import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import kotlinx.coroutines.flow.Flow

interface AprsAccess {
    val incoming: Flow<CapturedPacket>

    fun listenForAudioPackets(): Flow<CapturedPacket>
    fun listenForInternetPackets(settings: ConnectionConfiguration): Flow<CapturedPacket>
    fun findRecent(count: Int): Flow<List<CapturedPacket>>
    fun findById(id: Long): Flow<CapturedPacket?>
    fun transmitAudioPacket(packet: AprsPacket, encodingConfig: EncodingConfig, transmitConfig: AfskModulationConfiguration)
}
