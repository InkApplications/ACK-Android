package com.inkapplications.ack.android.settings.transformer

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Transform a duration to a primitive stored in milliseconds.
 *
 * Note: This is stored as an Int, so this should not be used for large
 * duration values.
 */
object MillisecondTransformer: Transformer<Duration, Int> {
    override fun toStorage(data: Duration): Int = data.inWholeMilliseconds.toInt()
    override fun toData(storage: Int): Duration = storage.milliseconds
}
