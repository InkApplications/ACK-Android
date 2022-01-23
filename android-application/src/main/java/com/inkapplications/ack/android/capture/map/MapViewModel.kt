package com.inkapplications.ack.android.capture.map

import com.inkapplications.ack.android.capture.log.LogItemViewModel
import com.inkapplications.ack.android.map.MarkerViewModel

/**
 * Model the state of the entire map page.
 *
 * This can't include events that are dependent on the map, which loads
 * separately.
 */
data class MapViewModel(
    val markers: Collection<MarkerViewModel> = emptyList(),
    val selectedItem: LogItemViewModel? = null,
    val trackPosition: Boolean = false,
) {
    val selectedItemVisible = selectedItem != null
}
