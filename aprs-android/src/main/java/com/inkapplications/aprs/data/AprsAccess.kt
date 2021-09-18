package com.inkapplications.aprs.data

import kotlinx.coroutines.flow.Flow

interface AprsAccess {
    val incoming: Flow<CapturedPacket>

    fun listenForAudioPackets(): Flow<CapturedPacket>
    fun listenForInternetPackets(settings: ConnectionSettings): Flow<CapturedPacket>
    fun findRecent(count: Int): Flow<List<CapturedPacket>>
    fun findById(id: Long): Flow<CapturedPacket?>
}

