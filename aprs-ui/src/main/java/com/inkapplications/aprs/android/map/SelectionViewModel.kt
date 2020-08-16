package com.inkapplications.aprs.android.map

import com.inkapplications.aprs.android.log.LogItem

/**
 * View data for a selected map item.
 */
data class SelectionViewModel(
    val visible: Boolean,
    val item: LogItem?
)
