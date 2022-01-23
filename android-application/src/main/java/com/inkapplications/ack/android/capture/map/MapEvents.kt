package com.inkapplications.ack.android.capture.map

import com.inkapplications.ack.android.map.Map
import kotlinx.coroutines.flow.*

/**
 * Provide access to map events.
 */
class MapEvents(
    private val mapData: MapDataRepository,
    private val map: Map,
) {
    private val selectedItem = map.selectedId.flatMapLatest {
        it?.let { mapData.findLogItem(it) } ?: flowOf(null)
    }

    val viewState: Flow<MapViewModel> = mapData.findMarkers()
        .map { MapViewModel(markers = it) }
        .combine(selectedItem) { viewModel, selectedItem ->
            viewModel.copy(selectedItem = selectedItem)
        }
        .combine(map.trackingState) { viewModel, tracking ->
            viewModel.copy(trackPosition = tracking)
        }
        .distinctUntilChanged()
}
