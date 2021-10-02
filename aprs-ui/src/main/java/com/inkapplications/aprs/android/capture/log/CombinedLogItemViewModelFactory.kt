package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.locale.format
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.karps.structures.AprsPacket
import dagger.Reusable
import javax.inject.Inject

@Reusable
class CombinedLogItemViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory
): LogItemViewModelFactory {
    override fun create(
        id: Long,
        packet: AprsPacket,
        metric: Boolean,
    ): LogItemViewModel {
        return LogItemViewModel(
            id = id,
            origin = packet.source.toString(),
            comment = when (packet) {
                is AprsPacket.Position -> "🌎 ${packet.comment.takeIfNotEmpty() ?: "Location Data" }"
                is AprsPacket.Weather -> "🌡 ${packet.temperature?.format(metric).takeIfNotEmpty() ?: "Weather Data" }"
                is AprsPacket.ObjectReport -> "📍 ${packet.comment.takeIfNotEmpty() ?: "Object Report" }"
                is AprsPacket.ItemReport -> "📦 ${packet.comment.takeIfNotEmpty() ?: "Item Report" }"
                is AprsPacket.Message -> "✉️ ${packet.addressee} ${packet.message} ${packet.messageNumber?.let { "($it)" }.orEmpty()}"
                is AprsPacket.TelemetryReport -> "\uD83D\uDCE1 ${packet.comment.takeIfNotEmpty() ?: "Telemetry Report"}"
                is AprsPacket.StatusReport -> "✅ ${packet.status.takeIfNotEmpty() ?: "Status Report"}"
                is AprsPacket.CapabilityReport -> "\uD83D\uDCD1 ${packet.capabilityData}"
                is AprsPacket.Unknown -> "⚠️ Unknown data"
            },
            symbol = when (packet) {
                is AprsPacket.Position -> packet.symbol.let(symbolFactory::createSymbol)
                is AprsPacket.Weather -> packet.symbol?.let(symbolFactory::createSymbol)
                else -> symbolFactory.defaultSymbol
            }
        )
    }

    private fun String?.takeIfNotEmpty() = this?.takeIf { it.isNotEmpty() }
}
