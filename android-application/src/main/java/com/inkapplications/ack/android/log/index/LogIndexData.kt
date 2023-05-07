package com.inkapplications.ack.android.log.index

import com.inkapplications.ack.data.CapturedPacket

/**
 * Data required to render the Log Index's view state.
 */
data class LogIndexData(
    /**
     * Whether units should be displayed in metric
     */
    val metric: Boolean,

    /**
     * List of packet data to be displayed in the index.
     */
    val packets: List<CapturedPacket>,
)
