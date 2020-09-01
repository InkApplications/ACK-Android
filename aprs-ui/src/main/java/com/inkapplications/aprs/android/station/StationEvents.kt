package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class StationEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val symbolFactory: SymbolFactory,
    private val logger: KimchiLogger
)  {
    fun stateEvents(id: Long): Flow<StationViewModel> {
        return aprs.findById(id).map { createViewModel(it) }
    }

    private fun createViewModel(packet: CapturedPacket?): StationViewModel {
        return when (val data = packet?.data) {
            is AprsPacket.Position -> StationViewModel(
                markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
                center = data.coordinates,
                zoom = ZoomLevels.ROADS,
                name = data.source.toString(),
                comment = data.comment
            )
            else -> StationViewModel(
                markers = emptyList(),
                center = Coordinates(Latitude(0.0), Longitude(0.0)),
                zoom = ZoomLevels.MIN,
                name = data?.source?.toString().orEmpty(),
                comment = data?.body.orEmpty()
            )
        }
    }
}
