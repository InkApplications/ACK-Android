package com.inkapplications.ack.data.adapters

import app.cash.sqldelight.ColumnAdapter
import com.inkapplications.ack.data.CaptureId

/**
 * SQLDelight ColumnAdapter for storing a CaptureId as a long.
 */
internal object CaptureIdColumnAdapter: ColumnAdapter<CaptureId, Long> {
    override fun decode(databaseValue: Long): CaptureId = CaptureId(databaseValue)
    override fun encode(value: CaptureId): Long = value.value
}
