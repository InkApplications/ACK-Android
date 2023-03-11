package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.log.LogItemViewState

/**
 * Model the state of the entire map page.
 *
 * This can't include events that are dependent on the map, which loads
 * separately.
 */
data class MapViewState(
    val markers: Collection<MarkerViewState> = emptyList(),
    val selectedItem: LogItemViewState? = null,
    val trackPosition: Boolean = false,
) {
    val selectedItemVisible = selectedItem != null
}
