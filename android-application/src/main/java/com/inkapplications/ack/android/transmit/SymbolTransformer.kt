package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.Transformer
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.symbolOf
import com.inkapplications.ack.structures.toTableCodePair

/**
 * Transform an APRS symbol into its 2-char ASCII code.
 */
object SymbolTransformer: Transformer<Symbol, String> {
    override fun toStorage(data: Symbol): String {
        return data.toTableCodePair().let { "${it.first}${it.second}" }
    }

    override fun toData(storage: String): Symbol {
        return symbolOf(storage[0], storage[1])
    }
}
