package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.transformer.Transformer
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.code
import com.inkapplications.ack.structures.symbolOf

/**
 * Transform an APRS symbol into its 2-char ASCII code.
 */
object SymbolTransformer: Transformer<Symbol, String> {
    override fun toStorage(data: Symbol): String = data.code

    override fun toData(storage: String): Symbol = symbolOf(storage)
}
