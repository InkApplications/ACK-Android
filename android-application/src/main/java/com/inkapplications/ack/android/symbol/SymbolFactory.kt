package com.inkapplications.ack.android.symbol

import android.graphics.Bitmap
import com.inkapplications.karps.structures.Symbol

/**
 * Create bitmaps for APRS Symbols.
 */
interface SymbolFactory {
    val defaultSymbol: Bitmap?
    fun createSymbol(symbol: Symbol): Bitmap?
}
