package com.inkapplications.aprs.android.log

import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.kotlin.mapEach
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogDataAccess @Inject constructor(
    aprs: AprsAccess,
    symbols: SymbolFactory
) {
    val items = aprs.findRecent(500).mapEach {
        LogItem(it.data, symbols)
    }
}
