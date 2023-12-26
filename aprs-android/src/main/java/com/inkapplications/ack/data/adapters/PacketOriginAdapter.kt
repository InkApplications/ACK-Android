package com.inkapplications.ack.data.adapters

import app.cash.sqldelight.ColumnAdapter
import com.inkapplications.ack.data.PacketSource

/**
 * SQLDelight ColumnAdapter for storing a PacketSource as its string name.
 */
internal object PacketSourceAdapter: ColumnAdapter<PacketSource, String> {
    override fun decode(databaseValue: String): PacketSource = PacketSource.valueOf(databaseValue)
    override fun encode(value: PacketSource): String = value.name
}
