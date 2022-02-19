package com.inkapplications.ack.android.symbol

import android.graphics.Bitmap
import com.inkapplications.ack.structures.Symbol

object SymbolFactoryStub: SymbolFactory {
    override val defaultSymbol: Bitmap? = null
    override fun createSymbol(symbol: Symbol): Bitmap? = null
}
