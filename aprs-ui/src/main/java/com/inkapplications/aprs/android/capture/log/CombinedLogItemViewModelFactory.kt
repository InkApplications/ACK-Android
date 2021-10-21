package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.locale.format
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.capabilities.Mapable
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
                is AprsPacket.Position -> "Position${packet.comment.append()}"
                is AprsPacket.Weather -> "Weather${packet.temperature?.format(metric).append()}"
                is AprsPacket.ObjectReport -> "Object: ${packet.name}${packet.comment.append(" - ")}"
                is AprsPacket.ItemReport -> "Item: ${packet.name}${packet.comment.append(" - ")}"
                is AprsPacket.Message -> "[${packet.addressee}] ${packet.message}${packet.messageNumber?.let { " ($it)" }.orEmpty()}"
                is AprsPacket.TelemetryReport -> "Telemetry${packet.comment.append()}"
                is AprsPacket.StatusReport -> "Status${packet.status.append()}"
                is AprsPacket.CapabilityReport -> "Capability Report"
                is AprsPacket.Unknown -> "Unknown data"
            },
            symbol = when (packet) {
                is Mapable -> packet.symbol?.let(symbolFactory::createSymbol)
                else -> symbolFactory.defaultSymbol
            }
        )
    }

    private fun String?.append(separator: String = ": ") = this.takeIfNotEmpty()?.let { "${separator}$it" }.orEmpty()
    private fun String?.takeIfNotEmpty() = this?.takeIf { it.isNotBlank() }
}
