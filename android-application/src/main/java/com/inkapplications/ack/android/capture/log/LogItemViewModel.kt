package com.inkapplications.ack.android.capture.log

import android.graphics.Bitmap

data class LogItemViewModel(
    val id: Long,
    val origin: String,
    val comment: String,
    val symbol: Bitmap?
)
