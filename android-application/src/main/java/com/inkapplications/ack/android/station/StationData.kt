package com.inkapplications.ack.android.station

import com.inkapplications.ack.data.CapturedPacket

/**
 * Data required to render the station screen.
 */
data class StationData(
    val packets: List<CapturedPacket> = emptyList(),
    val metric: Boolean = false,
)
