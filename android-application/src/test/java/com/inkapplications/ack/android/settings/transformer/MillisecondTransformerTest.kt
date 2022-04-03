package com.inkapplications.ack.android.settings.transformer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class MillisecondTransformerTest {
    @Test
    fun toStorage() {
        val result = MillisecondTransformer.toStorage(45.seconds)

        assertEquals(45000, result)
    }

    @Test
    fun fromStorage() {
        val result = MillisecondTransformer.toData(45000)

        assertEquals(45.seconds, result)
    }
}
