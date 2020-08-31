package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.map.Map
import kotlinx.coroutines.flow.*

/**
 * Manage the map's data and states.
 */
class MapManager(
    private val mapData: MapDataRepository,
    private val map: Map
) {
    val selectionState: Flow<SelectionViewModel> = map.selectedId
        .flatMapLatest { it?.let { mapData.findLogItem(it) } ?: flowOf(null) }
        .map {
            SelectionViewModel(
                visible = it != null,
                item = it
            )
        }

    suspend fun displayMarkers() {
        mapData.findMarkers().collect {
            map.showMarkers(it)
        }
    }
}
