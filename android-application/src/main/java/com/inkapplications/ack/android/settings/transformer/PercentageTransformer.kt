package com.inkapplications.ack.android.settings.transformer

import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.structure.roundToInt

/**
 * Transform a percentage object to a whole integer value.
 */
object PercentageTransformer: Transformer<Percentage, Int> {
    override fun toStorage(data: Percentage): Int = data.toWholePercentage().roundToInt()
    override fun toData(storage: Int): Percentage = storage.percent
}
