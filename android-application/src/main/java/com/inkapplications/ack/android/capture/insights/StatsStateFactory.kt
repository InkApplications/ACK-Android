package com.inkapplications.ack.android.capture.insights

import dagger.Reusable
import javax.inject.Inject

/**
 * Creates stats view state models based on current data.
 */
@Reusable
class StatsStateFactory @Inject constructor() {
    /**
     * Generate a view state for the given statistics.
     *
     * @param packetCount The total number of packets that have been collected.
     * @param stationCount The number of distinct stations that have reported packets.
     */
    fun createState(packetCount: Long, stationCount: Long): InsightsStatsState {
        return when (packetCount) {
            0L -> InsightsStatsState.None
            else -> InsightsStatsState.LoadedData(
                packets = packetCount,
                stations = stationCount,
            )
        }
    }
}
