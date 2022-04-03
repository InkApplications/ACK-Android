package com.inkapplications.ack.android.settings.transformer

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Transform a duration to a primitive stored in minutes.
 */
object MinuteTransformer: Transformer<Duration, Int> {
    override fun toStorage(data: Duration): Int = data.inWholeMinutes.toInt()
    override fun toData(storage: Int): Duration = storage.minutes
}
