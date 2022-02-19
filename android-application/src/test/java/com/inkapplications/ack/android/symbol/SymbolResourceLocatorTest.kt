package com.inkapplications.ack.android.symbol

import com.inkapplications.ack.structures.symbolOf
import kimchi.logger.EmptyLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SymbolResourceLocatorTest {
    @Test
    fun primaryResources() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_0", locator.getBaseResourceName(symbolOf('/', '!')))
        assertEquals("symbol_93", locator.getBaseResourceName(symbolOf('/', '~')))
    }

    @Test
    fun alternateResources() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_96", locator.getBaseResourceName(symbolOf('\\', '!')))
        assertEquals("symbol_189", locator.getBaseResourceName(symbolOf('\\', '~')))
    }

    @Test
    fun overlayResources() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_207", locator.getOverlayResourceName(symbolOf('0', '#')))
        assertEquals("symbol_216", locator.getOverlayResourceName(symbolOf('9', 'z')))
        assertEquals("symbol_224", locator.getOverlayResourceName(symbolOf('A', 'A')))
        assertEquals("symbol_249", locator.getOverlayResourceName(symbolOf('Z', '^')))
    }

    @Test
    fun unsupportedOverlay() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertNull(locator.getOverlayResourceName(symbolOf('0', '!')))
        assertNull(locator.getOverlayResourceName(symbolOf('9', '~')))
        assertNull(locator.getOverlayResourceName(symbolOf('A', '1')))
        assertNull(locator.getOverlayResourceName(symbolOf('Z', 'B')))
    }
}
