package com.inkapplications.ack.android.settings.transformer

import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

/**
 * Transform a length to a primitive of Miles.
 */
object MileTransformer: Transformer<Length, Int> {
    override fun toStorage(data: Length): Int = data.value(Miles).roundToInt()
    override fun toData(storage: Int): Length = Miles.of(storage)
}
