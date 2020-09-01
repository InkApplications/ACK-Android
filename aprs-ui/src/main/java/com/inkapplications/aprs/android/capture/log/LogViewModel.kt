package com.inkapplications.aprs.android.capture.log

import android.graphics.Bitmap

data class LogViewModel(
    val origin: String,
    val comment: String,
    val symbol: Bitmap
)
