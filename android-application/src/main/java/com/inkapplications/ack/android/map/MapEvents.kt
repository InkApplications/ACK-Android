package com.inkapplications.ack.android.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import com.inkapplications.ack.android.maps.CameraPositionDefaults
import com.inkapplications.ack.android.maps.MapCameraPosition
import com.inkapplications.ack.android.maps.MapViewModel
import com.inkapplications.ack.android.maps.ZoomLevels
import com.inkapplications.ack.data.CaptureId
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
    location: LocationAccess,
    context: Context,
) {
    val trackingEnabled = MutableStateFlow(false)
    val selectedItemId = MutableStateFlow<CaptureId?>(null)
    private val selectedItem = selectedItemId.flatMapLatest {
        it?.let { mapData.findLogItem(it) } ?: flowOf(null)
    }
    private val lastLocation = if (ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
        location.lastKnownLocation?.location
    } else null


    val viewState = combine(
        mapData.findMarkers(),
        trackingEnabled,
        selectedItem,
    ) { markers, tracking, selected ->
        MapViewState(
            MapViewModel(
                cameraPosition = (lastLocation ?: markers.firstOrNull()?.coordinates)
                    ?.let { MapCameraPosition(it, ZoomLevels.RIVERS) }
                    ?: CameraPositionDefaults.unknownLocation,
                markers = markers,
                enablePositionTracking = tracking
            ),
            selectedItem = selected,
        )
    }.distinctUntilChanged()
}
