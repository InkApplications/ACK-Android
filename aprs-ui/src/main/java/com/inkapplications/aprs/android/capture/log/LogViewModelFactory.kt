package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.karps.structures.AprsPacket
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory
) {
    fun create(packet: AprsPacket): LogViewModel {
        return LogViewModel(
            origin = packet.source.toString(),
            comment = when (packet) {
                is AprsPacket.Position -> packet.comment
                is AprsPacket.Weather -> "🌡 ${packet.temperature}"
                is AprsPacket.Unknown -> "⚠️ ${packet.body}"
            },
            symbol = when (packet) {
                is AprsPacket.Position -> packet.symbol.let(symbolFactory::createSymbol)
                is AprsPacket.Weather -> packet.symbol?.let(symbolFactory::createSymbol) ?: symbolFactory.defaultSymbol
                else -> symbolFactory.defaultSymbol
            }
        )
    }
}
