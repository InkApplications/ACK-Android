package com.inkapplications.ack.android.capture.insights

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StatsStateFactoryTest {
    private val factory = StatsStateFactory()

    @Test
    fun completeStats() {
        val result = factory.createState(
            packetCount = 123,
            stationCount = 456,
        )

        assertTrue(result is InsightsStatsState.LoadedData)
        assertEquals(123, result.packets)
        assertEquals(456, result.stations)
    }

    @Test
    fun noStats() {
        val result = factory.createState(
            packetCount = 0,
            stationCount = 0,
        )

        assertTrue(result is InsightsStatsState.None)
    }
}
