package com.inkapplications.ack.android.map

/**
 * State container for possible map data/settings.
 */
data class MapState(
    /**
     * Collection of marker states to add to the map.
     */
    val markers: List<MarkerViewState> = emptyList(),

    /**
     * Camera position of the map.
     */
    val mapCameraPosition: MapCameraPosition = CameraPositionDefaults.unknownLocation,
)
