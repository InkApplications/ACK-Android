package com.inkapplications.ack.android.settings.transformer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class MinuteTransformerTest {
    @Test
    fun toStorage() {
        val result = MinuteTransformer.toStorage(1.5.hours)

        assertEquals(90, result)
    }

    @Test
    fun fromStorage() {
        val result = MinuteTransformer.toData(90)

        assertEquals(1.5.hours, result)
    }
}
