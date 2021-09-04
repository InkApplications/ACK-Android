package com.inkapplications.aprs.data

import kotlinx.coroutines.flow.Flow

interface AprsAccess {
    val incomingAudio: Flow<CapturedPacket>
    val incomingInternet: Flow<CapturedPacket>

    fun findRecent(count: Int): Flow<List<CapturedPacket>>
    fun findById(id: Long): Flow<CapturedPacket?>
}

