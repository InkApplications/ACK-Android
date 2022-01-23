package com.inkapplications.ack.android.symbol

import com.inkapplications.karps.structures.Symbol
import dagger.Reusable
import kimchi.logger.KimchiLogger
import javax.inject.Inject

private const val ALT_START = 96
private const val OVERLAY_START = 207
private const val PREFIX = "symbol_"
private const val CHAR_RANGE_START = '!'
private const val CHAR_RANGE_END = '~'
private const val ASC_0 = '0'
private const val ASC_9 = '9'
private const val ASC_A = 'A'
private const val ASC_Z = 'Z'
private const val FAULT_SYMBOL = 127.toChar()

/**
 * Translate a pair of APRS Symbol Chars to symbol resource names.
 *
 * The resource names are arbitrary and sequential as produced by an image
 * slicer. They start at 0 and go up, with some empty symbols having been
 * removed.
 * The three tables (primary, offset and alphanumeric overlays) are offset
 * by [ALT_START] and [OVERLAY_START]. i.e. the alternate symbols should start
 * at the image number corresponding to [ALT_START] and so on.
 *
 * APRS Symbol format is defined here: http://www.aprs.org/symbols.html
 *
 * TL;DR – the first char is typically '/' or '\' designating the primary or
 * secondary set of symbols, and the symbol is looked up by the second
 * character's ASCII table position. If the first character is something other
 * than '/' or '\' then it is indicating an overlay.
 *
 * This supports alphanumeric overlays, and will fall-back to just the alternate
 * symbol if an unknown overlay is used.
 */
@Reusable
class SymbolResourceLocator @Inject constructor(
    private val logger: KimchiLogger
) {
    fun getBaseResourceName(symbol: Symbol): String {
        if (symbol.id !in CHAR_RANGE_START..CHAR_RANGE_END) {
            logger.warn("Out-of-bounds symbol requested: ${symbol.id}")
            return format(FAULT_SYMBOL - CHAR_RANGE_START)
        }
        return when(symbol) {
            is Symbol.Primary -> format(symbol.id - CHAR_RANGE_START)
            is Symbol.Alternate -> format(symbol.id - CHAR_RANGE_START + ALT_START)
        }
    }

    fun getOverlayResourceName(symbol: Symbol): String? {
        if (symbol !is Symbol.Alternate) return null
        val overlay = symbol.overlay ?: return null
        if (!symbol.alphaNumeric) return null
        return when(overlay) {
            in ASC_0..ASC_9, in ASC_A..ASC_Z -> format(overlay - ASC_0 + OVERLAY_START)
            else -> null.also {
                logger.warn("Out-of-bounds overlay requested: ${overlay.toInt()}")
            }
        }
    }

    private fun format(number: Int): String = PREFIX + number.toString()
}
