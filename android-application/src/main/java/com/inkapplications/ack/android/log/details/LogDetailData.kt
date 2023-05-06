package com.inkapplications.ack.android.log.details

import com.inkapplications.ack.data.CapturedPacket

/**
 * Raw Data required to render the Log Details UI.
 */
data class LogDetailData(
    /**
     * The packet that is being displayed
     */
    val packet: CapturedPacket,

    /**
     * Whether to display stats in metric format.
     */
    val metric: Boolean = false,

    /**
     * Whether debug information should be visible.
     */
    val debug: Boolean = false,
)
