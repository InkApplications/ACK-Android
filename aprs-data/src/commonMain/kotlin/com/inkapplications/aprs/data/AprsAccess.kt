package com.inkapplications.aprs.data

import kotlinx.coroutines.flow.Flow

interface AprsAccess {
    val data: Flow<AprsPacket>
}


