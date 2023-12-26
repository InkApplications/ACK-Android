package com.inkapplications.ack.data.adapters

import app.cash.sqldelight.ColumnAdapter
import com.inkapplications.ack.structures.station.Callsign

/**
 * SQLDelight ColumnAdapter for storing a Callsign as its canonical string.
 */
internal object CallsignAdapter: ColumnAdapter<Callsign, String> {
    override fun decode(databaseValue: String): Callsign = Callsign(databaseValue)
    override fun encode(value: Callsign): String = value.canonical.uppercase()
}
