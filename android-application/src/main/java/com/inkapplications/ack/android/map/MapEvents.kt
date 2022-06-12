package com.inkapplications.ack.android.map

import com.inkapplications.android.extensions.location.LocationAccess
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provide access to map events.
 */
@Singleton
class MapEvents @Inject constructor(
    private val mapData: MapDataRepository,
    private val location: LocationAccess,
) {
    val trackingEnabled = MutableStateFlow(false)
    val selectedItemId = MutableStateFlow<Long?>(null)
    private val selectedItem = selectedItemId.flatMapLatest {
        it?.let { mapData.findLogItem(it) } ?: flowOf(null)
    }

    val initialState get() =
        location.lastKnownLocation
            ?.let { MapCameraPosition(it.location, ZoomLevels.RIVERS) }
            ?: CameraPositionDefaults.unknownLocation

    val viewState: Flow<MapViewModel> = mapData.findMarkers()
        .map { MapViewModel(markers = it) }
        .combine(selectedItem) { viewModel, selectedItem ->
            viewModel.copy(selectedItem = selectedItem)
        }
        .combine(trackingEnabled) { viewModel, tracking ->
            viewModel.copy(trackPosition = tracking)
        }
        .distinctUntilChanged()
}
