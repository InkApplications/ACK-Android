package com.inkapplications.ack.android.settings.transformer

import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.measure.us.toMiles
import inkapplications.spondee.structure.roundToInt

/**
 * Transform a length to a primitive of Miles.
 */
object MileTransformer: Transformer<Length, Int> {
    override fun toStorage(data: Length): Int = data.toMiles().roundToInt()
    override fun toData(storage: Int): Length = storage.miles
}
