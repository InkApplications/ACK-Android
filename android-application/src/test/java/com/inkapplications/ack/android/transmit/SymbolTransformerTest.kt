package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.structures.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolTransformerTest {
    @Test
    fun toStorage() {
        assertEquals("/$", Symbol.Primary('$').let(SymbolTransformer::toStorage))
    }

    @Test
    fun toData() {
        assertEquals(Symbol.Primary('$'), SymbolTransformer.toData("/$"))
    }
}
