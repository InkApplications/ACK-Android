package com.inkapplications.ack.android.symbol

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.inkapplications.ack.structures.Symbol
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Viewmodel used to in UIs that require symbol rendering.
 *
 * This provides access to the symbol factory to create bitmaps in Compose
 * as well as data access to the available list of symbols in APRS.
 */
@HiltViewModel
class SymbolSelectorViewModel @Inject constructor(
    private val symbolFactory: SymbolFactory,
): ViewModel() {
    val symbols = Symbol.Primary.ALL + Symbol.Alternate.BASE
    fun createBitmap(symbol: Symbol): Bitmap? {
        return symbolFactory.createSymbol(symbol) ?: symbolFactory.defaultSymbol
    }
}
