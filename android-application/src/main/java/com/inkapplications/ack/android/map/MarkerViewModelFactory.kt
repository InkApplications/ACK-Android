package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.symbol.SymbolFactory
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.symbolOf
import com.inkapplications.android.extensions.ViewModelFactory
import javax.inject.Inject

/**
 * Convert packet data into the model used to render the marker on a map.
 *
 * If the packet is not mapable for whatever reason, this will return a
 * null marker.
 */
class MarkerViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory,
): ViewModelFactory<CapturedPacket, MarkerViewModel?> {
    private val defaultWeatherSymbol = symbolOf('/', 'W')

    override fun create(data: CapturedPacket): MarkerViewModel? {
        val mapable = data.parsed.data as? Mapable ?: return null
        val coordinates = mapable.coordinates ?: return null
        val symbol = mapable.symbol
            ?: defaultWeatherSymbol.takeIf { data.parsed.data is PacketData.Weather }
            ?: return null
        val symbolBitmap = symbolFactory.createSymbol(symbol) ?: return null

        return MarkerViewModel(data.id, coordinates, symbolBitmap)
    }
}
