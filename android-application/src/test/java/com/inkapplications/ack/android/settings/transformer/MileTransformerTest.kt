package com.inkapplications.ack.android.settings.transformer

import inkapplications.spondee.measure.Miles
import kotlin.test.Test
import kotlin.test.assertEquals

class MileTransformerTest {
    @Test
    fun toStorage() {
        assertEquals(5, Miles.of(5).let(MileTransformer::toStorage))
        assertEquals(2, Miles.of(1.5).let(MileTransformer::toStorage))
    }

    @Test
    fun toData() {
        assertEquals(Miles.of(5), MileTransformer.toData(5))
    }
}
