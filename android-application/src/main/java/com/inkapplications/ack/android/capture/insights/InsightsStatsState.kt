package com.inkapplications.ack.android.capture.insights

/**
 * States for the "stats" section of the insight screen.
 */
sealed interface InsightsStatsState {
    /**
     * Indicates that no data has been loaded yet.
     */
    object Initial: InsightsStatsState

    /**
     * Indicates that data has been loaded, but the user has no packets
     * to generate stats from.
     */
    object None: InsightsStatsState

    /**
     * Loaded Stats about captured packets
     */
    data class LoadedData(
        /**
         * Total number of captured packets.
         */
        val packets: Int,

        /**
         * Distinct stations that have been seen in the captured packets.
         */
        val stations: Int,
    ): InsightsStatsState
}
