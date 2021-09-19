package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.connection.ConnectionSettings
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeString
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
