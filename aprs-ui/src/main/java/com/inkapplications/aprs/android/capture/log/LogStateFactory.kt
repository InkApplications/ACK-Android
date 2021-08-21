package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.karps.structures.AprsPacket
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogStateFactory @Inject constructor(
    private val symbolFactory: SymbolFactory
) {
    fun create(id: Long, packet: AprsPacket): LogItemState {
        return LogItemState(
            id = id,
            origin = packet.source.toString(),
            comment = when (packet) {
                is AprsPacket.Position -> "🌎 ${packet.coordinates} ${packet.comment}"
                is AprsPacket.Weather -> "🌡 ${packet.temperature}"
                is AprsPacket.Unknown -> "⚠️ ${packet.body}"
                is AprsPacket.ObjectReport -> "📍 ${packet.comment} @ ${packet.coordinates}"
                is AprsPacket.ItemReport -> "📦 ${packet.comment} @ ${packet.coordinates}"
                is AprsPacket.Message -> "✉️ ${packet.addressee} ${packet.message} ${packet.messageNumber?.let { "($it)" }.orEmpty()}"
                is AprsPacket.TelemetryReport -> "\uD83D\uDCE1 ${packet.comment}"
                is AprsPacket.StatusReport -> "✅ ${packet.status}"
                is AprsPacket.CapabilityReport -> "\uD83D\uDCD1 ${packet.capabilityData}"
            },
            symbol = when (packet) {
                is AprsPacket.Position -> packet.symbol.let(symbolFactory::createSymbol)
                is AprsPacket.Weather -> packet.symbol?.let(symbolFactory::createSymbol)
                else -> symbolFactory.defaultSymbol
            }
        )
    }
}
