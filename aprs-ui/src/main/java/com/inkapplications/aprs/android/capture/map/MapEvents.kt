package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.map.Map
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.combineTriple
import kotlinx.coroutines.flow.*

/**
 * Provide access to map events.
 */
class MapEvents(
    private val mapData: MapDataRepository,
    private val map: Map
) {
    private val selectedItem = map.selectedId.flatMapLatest {
        it?.let { mapData.findLogItem(it) } ?: flowOf(null)
    }

    val viewState: Flow<MapViewModel> = mapData.findMarkers()
        .combinePair(selectedItem)
        .combineTriple(map.trackingState)
        .map { (markers, selected, tracking) ->
            MapViewModel(markers, selected, tracking)
        }
        .distinctUntilChanged()
}
