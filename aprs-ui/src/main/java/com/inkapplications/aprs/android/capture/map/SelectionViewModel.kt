package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.capture.log.LogItem

/**
 * View data for a selected map item.
 */
data class SelectionViewModel(
    val visible: Boolean,
    val item: LogItem?
)
