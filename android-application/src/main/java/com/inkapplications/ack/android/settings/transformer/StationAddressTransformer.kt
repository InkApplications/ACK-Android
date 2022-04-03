package com.inkapplications.ack.android.settings.transformer

import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.station.toStationAddress

/**
 * Transform a station callsign and ssid to a string primitive.
 */
object StationAddressTransformer: Transformer<StationAddress, String> {
    override fun toStorage(data: StationAddress): String = data.toString()
    override fun toData(storage: String): StationAddress = storage.toStationAddress()
}
