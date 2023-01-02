package com.inkapplications.ack.android.settings.transformer

import inkapplications.spondee.measure.us.miles
import kotlin.test.Test
import kotlin.test.assertEquals

class MileTransformerTest {
    @Test
    fun toStorage() {
        assertEquals(5, 5.miles.let(MileTransformer::toStorage))
        assertEquals(2, 1.5.miles.let(MileTransformer::toStorage))
    }

    @Test
    fun toData() {
        assertEquals(5.miles, MileTransformer.toData(5))
    }
}
