package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.transformer.PercentageTransformer
import inkapplications.spondee.scalar.percent
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageTransformer {
    @Test
    fun toData() {
        assertEquals(69.percent, PercentageTransformer.toData(69))
    }

    @Test
    fun toStorage() {
        assertEquals(69, 69.percent.let(PercentageTransformer::toStorage))
        assertEquals(70, 69.5.percent.let(PercentageTransformer::toStorage))
    }
}
