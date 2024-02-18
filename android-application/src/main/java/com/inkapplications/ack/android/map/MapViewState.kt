package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.maps.MapViewModel

/**
 * Model the state of the entire map page.
 *
 * This can't include events that are dependent on the map, which loads
 * separately.
 */
data class MapViewState(
    val mapViewModel: MapViewModel,
    val selectedItem: LogItemViewState? = null,
) {
    val selectedItemVisible = selectedItem != null
}
