package com.inkapplications.ack.android.settings.transformer

import com.inkapplications.ack.android.settings.Transformer
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

/**
 * Transform a percentage object to a whole integer value.
 */
object PercentageTransformer: Transformer<Percentage, Int> {
    override fun toStorage(data: Percentage): Int = data.value(WholePercentage).roundToInt()
    override fun toData(storage: Int): Percentage = WholePercentage.of(storage)
}
