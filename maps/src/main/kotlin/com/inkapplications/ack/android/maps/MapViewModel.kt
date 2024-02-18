package com.inkapplications.ack.android.maps

data class MapViewModel(
    val cameraPosition: MapCameraPosition,
    val markers: Collection<MarkerViewState>,
    val enablePositionTracking: Boolean = false,
)
