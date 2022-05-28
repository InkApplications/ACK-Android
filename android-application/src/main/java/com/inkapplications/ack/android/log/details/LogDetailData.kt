package com.inkapplications.ack.android.log.details

import com.inkapplications.ack.data.CapturedPacket

/**
 * Raw Data required to render the Log Details UI.
 */
data class LogDetailData(
    val packet: CapturedPacket,
    val metric: Boolean = false,
    val debug: Boolean = false,
)
