package com.inkapplications.aprs.android.symbol

import dagger.Reusable
import kimchi.logger.KimchiLogger
import javax.inject.Inject

private const val ALT_START = 96
private const val OVERLAY_START = 207
private const val PREFIX = "symbol_"
private const val PRIMARY = '/'
private const val CHAR_RANGE_START = '!'.toInt()
private const val CHAR_RANGE_END = '~'.toInt()
private const val ASC_0 = '0'.toInt()
private const val ASC_9 = '9'.toInt()
private const val ASC_A = 'A'.toInt()
private const val ASC_Z = 'Z'.toInt()
private const val FAULT_SYMBOL = 127
private val SUPPORTS_OVERLAY = arrayOf('#','&','0','>','A','W','^','_','s','u','v','z')

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
    fun getBaseResourceName(symbol: Pair<Char, Char>): String {
        val tableIdentifier = symbol.first
        val symbolNumber = symbol.second.toInt()

        if (symbolNumber !in CHAR_RANGE_START..CHAR_RANGE_END) {
            logger.warn("Out-of-bounds symbol requested: ${symbolNumber}")
            return format(FAULT_SYMBOL - CHAR_RANGE_START)
        }

        return when(tableIdentifier) {
            PRIMARY -> format(symbolNumber - CHAR_RANGE_START)
            else -> format(symbolNumber - CHAR_RANGE_START + ALT_START)
        }
    }

    fun getOverlayResourceName(symbol: Pair<Char, Char>): String? {
        if (symbol.second !in SUPPORTS_OVERLAY) {
            logger.warn("Unsupported overlay requested for icon: ${symbol.second.toInt()}")
            return null
        }
        return when(val overlayNumber = symbol.first.toInt()) {
            in ASC_0..ASC_9, in ASC_A..ASC_Z -> format(overlayNumber - ASC_0 + OVERLAY_START)
            else -> null.also {
                logger.warn("Out-of-bounds overlay requested: $overlayNumber")
            }
        }
    }

    private fun format(number: Int): String = PREFIX + number.toString()
}
