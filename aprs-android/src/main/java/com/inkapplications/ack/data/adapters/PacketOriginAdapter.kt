package com.inkapplications.ack.data.adapters

import app.cash.sqldelight.ColumnAdapter
import com.inkapplications.ack.data.PacketOrigin

/**
 * SQLDelight ColumnAdapter for storing a PacketSource as its string name.
 */
internal object PacketOriginAdapter: ColumnAdapter<PacketOrigin, String> {
    override fun decode(databaseValue: String): PacketOrigin = PacketOrigin.valueOf(databaseValue)
    override fun encode(value: PacketOrigin): String = value.name
}
