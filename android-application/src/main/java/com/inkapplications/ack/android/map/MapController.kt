package com.inkapplications.ack.android.map

import android.Manifest.permission
import androidx.annotation.RequiresPermission

/**
 * Actions that can be sent to control the map view.
 */
interface MapController {
    /**
     * Initialize the map with default settings
     */
    fun initDefaults()

    /**
     * Add padding to the bottom of the map.
     *
     * This is used when there is an element like a bottom bar obstructing
     * the full map, and ensures that elements are still displayed properly.
     */
    fun setBottomPadding(padding: Float)

    /**
     * Display a set of markers on the map.
     */
    fun showMarkers(markers: Collection<MarkerViewModel>)

    /**
     * Animate the map to a specific location and zoom level
     */
    fun zoomTo(cameraPosition: MapCameraPosition)

    /**
     * Immediately set the position and zoom level of the map to a location, without animating.
     */
    fun setCamera(cameraPosition: MapCameraPosition)

    /**
     * Display and zoom to the device's current location on the map.
     */
    @RequiresPermission(anyOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
    fun enablePositionTracking()

    /**
     * Disable the device's current location from being tracked and displayed.
     */
    fun disablePositionTracking()
}

