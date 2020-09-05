package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude
import dagger.Reusable
import javax.inject.Inject

@Reusable
class StationViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory
) {
    private val defaultWeatherSymbol = symbolOf('/', 'W')

    fun create(packet: CapturedPacket?) = when (val data = packet?.data) {
        is AprsPacket.Position -> StationViewModel(
            markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
            center = data.coordinates,
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.comment
        )
        is AprsPacket.Weather -> StationViewModel(
            markers = data.position?.let {
                listOf(MarkerViewModel(
                    id = packet.id,
                    coordinates = it,
                    symbol = symbolFactory.createSymbol(data.symbol ?: defaultWeatherSymbol)
                ))
            } ?: emptyList(),
            weather = "${data.temperature}",
            center = data.position ?: Coordinates(Latitude(0.0), Longitude(0.0)),
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.body
        )
        else -> StationViewModel(
            name = data?.source?.toString().orEmpty(),
            comment = data?.body.orEmpty()
        )
    }
}
