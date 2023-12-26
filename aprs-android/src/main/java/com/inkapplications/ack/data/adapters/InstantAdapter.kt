package com.inkapplications.ack.data.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

/**
 * SQLDelight ColumnAdapter for storing an Instant as a long in epoch milliseconds.
 */
internal object InstantAdapter: ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}
