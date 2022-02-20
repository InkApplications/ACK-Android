package com.inkapplications.ack.android.capture.map

import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.Map
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.ZoomLevels
import com.inkapplications.android.extensions.location.LocationAccess
import kotlinx.coroutines.flow.*

/**
 * Provide access to map events.
 */
class MapEvents(
    private val mapData: MapDataRepository,
    private val location: LocationAccess,
    private val map: Map,
) {
    private val selectedItem = map.selectedId.flatMapLatest {
        it?.let { mapData.findLogItem(it) } ?: flowOf(null)
    }

    val initialState get() =
        location.lastKnownLocation
            ?.let { MapCameraPosition(it.location, ZoomLevels.ROADS) }
            ?: CameraPositionDefaults.unknownLocation

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
