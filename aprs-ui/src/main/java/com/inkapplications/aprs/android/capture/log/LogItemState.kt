package com.inkapplications.aprs.android.capture.log

import android.graphics.Bitmap

data class LogItemState(
    val id: Long,
    val origin: String,
    val comment: String,
    val symbol: Bitmap?
)
