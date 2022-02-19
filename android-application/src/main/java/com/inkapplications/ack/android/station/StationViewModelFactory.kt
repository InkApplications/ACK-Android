package com.inkapplications.ack.android.station

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.map.MarkerViewModel
import com.inkapplications.ack.android.map.ZoomLevels
import com.inkapplications.ack.android.symbol.SymbolFactory
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.symbolOf
import dagger.Reusable
import inkapplications.spondee.measure.Length
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.value
import javax.inject.Inject
import kotlin.math.roundToInt

@Reusable
class StationViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory,
    private val stringResources: StringResources
) {
    private val defaultWeatherSymbol = symbolOf('/', 'W')

    fun create(
        packet: CapturedPacket?,
        metric: Boolean,
        showDebug: Boolean,
    ): StationViewModel {
        val data = packet?.parsed?.data

        val packetTypeData = when (data) {
            is PacketData.Position -> StationViewModel(
                markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
                center = data.coordinates,
                zoom = ZoomLevels.ROADS,
                comment = data.comment,
                altitude = data.altitude.distanceString(metric),
            )
            is PacketData.Weather -> StationViewModel(
                markers = data.coordinates?.let {
                    listOf(MarkerViewModel(
                        id = packet.id,
                        coordinates = it,
                        symbol = symbolFactory.createSymbol(data.symbol ?: defaultWeatherSymbol)
                    ))
                } ?: emptyList(),
                temperature = data.temperature?.format(metric).orEmpty(),
                wind = data.windString(metric),
                center = data.coordinates ?: GeoCoordinates(0.latitude, 0.longitude),
                zoom = ZoomLevels.ROADS,
            )
            is PacketData.ObjectReport -> StationViewModel(
                markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
                center = data.coordinates,
                zoom = ZoomLevels.ROADS,
                comment = data.comment,
                altitude = data.altitude.distanceString(metric),
            )
            is PacketData.ItemReport -> StationViewModel(
                markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
                center = data.coordinates,
                zoom = ZoomLevels.ROADS,
                comment = data.comment,
                altitude = data.altitude.distanceString(metric),
            )
            is PacketData.Message -> StationViewModel(
                comment = data.message,
            )
            is PacketData.TelemetryReport -> StationViewModel(
                comment = data.comment,
                telemetryValues = data.data,
                telemetrySequence = data.sequenceId,
            )
            is PacketData.StatusReport -> StationViewModel(
                comment = data.status,
            )
            is PacketData.CapabilityReport -> StationViewModel(
            )
            is PacketData.Unknown -> StationViewModel(
                comment = data.body,
            )
            null -> StationViewModel()
        }
        return packetTypeData.copy(
            name = packet?.parsed?.route?.source?.toString().orEmpty(),
            rawSource = packet?.raw?.decodeToString().orEmpty(),
            debugDataVisible = showDebug || data is PacketData.Unknown,
            receiveIcon = when (packet?.source) {
                PacketSource.Ax25 -> Icons.Default.SettingsInputAntenna
                PacketSource.AprsIs -> Icons.Default.Cloud
                null -> null
            },
            receiveIconDescription = when (packet?.source) {
                PacketSource.Ax25 -> "Radio Packet"
                PacketSource.AprsIs -> "Internet Packet"
                null -> null
            },
        )
    }

    private fun Length?.distanceString(metric: Boolean) = this?.format(metric).orEmpty()

    private fun PacketData.Weather.windString(metricUnits: Boolean): String {
        val direction = windData.direction?.value(Degrees)?.roundToInt()?.let { "${it}º" }
        val speed = windData.speed?.format(metricUnits)
        val gust = windData.gust?.format(metricUnits)

        return when {
            direction != null && speed != null && gust != null -> stringResources.getString(R.string.station_wind_format_full, direction, speed, gust)
            direction != null && speed != null -> stringResources.getString(R.string.station_wind_format_wind_only, direction, speed)
            direction != null -> stringResources.getString(R.string.station_wind_format_direction_only, direction)
            speed != null -> stringResources.getString(R.string.station_wind_format_speed_only, speed)
            else -> ""
        }
    }
}
