package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.transformer.Transformer
import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.station.toStationAddress

/**
 * Transforms a path of digipeaters into a comma-separated string.
 */
object PathTransformer: Transformer<List<Digipeater>, String> {
    override fun toStorage(data: List<Digipeater>): String {
        return data.map { it.address.toString() }.joinToString(",")
    }

    override fun toData(storage: String): List<Digipeater> {
        return storage.split(',')
            .map { it.trim() }
            .map { it.toStationAddress() }
            .map(::Digipeater)
    }
}
