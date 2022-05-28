package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.ack.structures.WindData
import inkapplications.spondee.measure.MilesPerHour
import inkapplications.spondee.spatial.Cardinal
import inkapplications.spondee.spatial.toAngle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SummaryFactoryTest {
    private val factory = SummaryFactory(ParrotStringResources)

    @Test
    fun noData() {
        val result = factory.createWindSummary(WindData(), false)

        assertNull(result)
    }

    @Test
    fun speedOnly() {
        val result = factory.createWindSummary(
            WindData(speed = MilesPerHour.of(12)),
            false,
        )

        assertEquals("12mph", result)
    }

    @Test
    fun directionOnly() {
        val result = factory.createWindSummary(
            WindData(direction = Cardinal.South.toAngle()),
            false,
        )

        assertEquals("180º", result)
    }

    @Test
    fun speedAndDirection() {
        val result = factory.createWindSummary(
            WindData(
                speed = MilesPerHour.of(12),
                direction = Cardinal.East.toAngle(),
            ),
            false,
        )

        assertEquals("90º|12mph", result)
    }

    @Test
    fun full() {
        val result = factory.createWindSummary(
            WindData(
                speed = MilesPerHour.of(12),
                direction = Cardinal.East.toAngle(),
                gust = MilesPerHour.of(34)
            ),
            false,
        )

        assertEquals("90º|12mph|34mph", result)
    }
}
