package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.symbol.SymbolFactory
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilities.Mapable
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
            origin = packet.route.source.toString(),
            comment = when (val data = packet.data) {
                is PacketData.Position -> "Position${data.comment.append()}"
                is PacketData.Weather -> "Weather${data.temperature?.format(metric).append()}"
                is PacketData.ObjectReport -> "Object: ${data.name}${data.comment.append(" - ")}"
                is PacketData.ItemReport -> "Item: ${data.name}${data.comment.append(" - ")}"
                is PacketData.Message -> "[${data.addressee}] ${data.message}${data.messageNumber?.let { " ($it)" }.orEmpty()}"
                is PacketData.TelemetryReport -> "Telemetry${data.comment.append()}"
                is PacketData.StatusReport -> "Status${data.status.append()}"
                is PacketData.CapabilityReport -> "Capability Report"
                is PacketData.Unknown -> "Unknown data"
            },
            symbol = when (val data = packet.data) {
                is Mapable -> data.symbol?.let(symbolFactory::createSymbol)
                else -> symbolFactory.defaultSymbol
            }
        )
    }

    private fun String?.append(separator: String = ": ") = this.takeIfNotEmpty()?.let { "${separator}$it" }.orEmpty()
    private fun String?.takeIfNotEmpty() = this?.takeIf { it.isNotBlank() }
}
