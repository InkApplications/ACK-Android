package com.inkapplications.ack.android.log

import android.graphics.Bitmap
import com.inkapplications.ack.structures.station.Callsign

data class LogItemViewModel(
    val id: Long,
    val source: Callsign,
    val origin: String,
    val comment: String,
    val symbol: Bitmap?
)
