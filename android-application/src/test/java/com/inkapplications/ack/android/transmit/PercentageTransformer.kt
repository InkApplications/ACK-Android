package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.transformer.PercentageTransformer
import inkapplications.spondee.scalar.WholePercentage
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageTransformer {
    @Test
    fun toData() {
        assertEquals(WholePercentage.of(69), PercentageTransformer.toData(69))
    }

    @Test
    fun toStorage() {
        assertEquals(69, WholePercentage.of(69).let(PercentageTransformer::toStorage))
        assertEquals(70, WholePercentage.of(69.5).let(PercentageTransformer::toStorage))
    }
}
