package com.inkapplications.aprs.android.symbol

import kimchi.logger.EmptyLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SymbolResourceLocatorTest {
    @Test
    fun primaryResources() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_0", locator.getBaseResourceName('/' to '!'))
        assertEquals("symbol_93", locator.getBaseResourceName('/' to '~'))
    }

    @Test
    fun alternateResources() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_96", locator.getBaseResourceName('\\' to '!'))
        assertEquals("symbol_189", locator.getBaseResourceName('\\' to '~'))
    }

    @Test
    fun baseOutBounds() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_94", locator.getBaseResourceName('/' to ' '))
        assertEquals("symbol_94", locator.getBaseResourceName('/' to 127.toChar()))
    }

    @Test
    fun baseAlternateFallback() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_96", locator.getBaseResourceName('0' to '!'))
        assertEquals("symbol_189", locator.getBaseResourceName('Z' to '~'))
        assertEquals("symbol_96", locator.getBaseResourceName('`' to '!'))
        assertEquals("symbol_189", locator.getBaseResourceName(127.toChar() to '~'))
    }

    @Test
    fun overlayResources() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertEquals("symbol_207", locator.getOverlayResourceName('0' to '#'))
        assertEquals("symbol_216", locator.getOverlayResourceName('9' to 'z'))
        assertEquals("symbol_224", locator.getOverlayResourceName('A' to 'A'))
        assertEquals("symbol_249", locator.getOverlayResourceName('Z' to '^'))
    }

    @Test
    fun unsupportedOverlay() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertNull(locator.getOverlayResourceName('0' to '!'))
        assertNull(locator.getOverlayResourceName('9' to '~'))
        assertNull(locator.getOverlayResourceName('A' to '1'))
        assertNull(locator.getOverlayResourceName('Z' to 'B'))
    }

    @Test
    fun overlayOutOfBounds() {
        val locator = SymbolResourceLocator(EmptyLogger)

        assertNull(locator.getOverlayResourceName('/' to '!'))
        assertNull(locator.getOverlayResourceName(':' to 'a'))
        assertNull(locator.getOverlayResourceName('@' to '1'))
        assertNull(locator.getOverlayResourceName('[' to 'A'))
    }
}
