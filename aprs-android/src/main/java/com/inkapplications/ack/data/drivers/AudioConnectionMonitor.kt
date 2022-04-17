package com.inkapplications.ack.data.drivers

import inkapplications.spondee.scalar.Percentage
import kotlinx.coroutines.flow.Flow

interface AudioConnectionMonitor {
    val volume: Flow<Percentage?>
}
