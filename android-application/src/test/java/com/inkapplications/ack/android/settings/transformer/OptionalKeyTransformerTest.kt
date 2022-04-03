package com.inkapplications.ack.android.settings.transformer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OptionalKeyTransformerTest {
    private val backingFake = object: Transformer<String, String> {
        override fun toStorage(data: String): String = "STORED"
        override fun toData(storage: String): String = "DATA"
    }
    val transformer = OptionalKeyTransformer("TestNull", backingFake)

    @Test
    fun toData() {
        assertEquals("DATA", transformer.toData("Test"))
        assertNull(transformer.toData("TestNull"))
    }

    @Test
    fun toStorage() {
        assertEquals("STORED", transformer.toStorage("Test"))
        assertEquals("TestNull", transformer.toStorage(null))
    }
}
