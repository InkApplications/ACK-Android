package com.inkapplications.ack.android.map

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.RequiresPermission
import com.google.gson.JsonObject
import com.inkapplications.ack.android.R
import com.inkapplications.android.continuePropagation
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.OnLocationCameraTransitionListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import inkapplications.spondee.spatial.GeoCoordinates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

/**
 * Manipulate the Mapbox Map in a generic way.
 */
class Map(
    private val context: Context,
    private val view: MapView,
    private val map: MapboxMap,
    private val style: Style,
    private val resources: Resources
) {
    private val selectedIdState = MutableStateFlow<Long?>(null)
    val selectedId = selectedIdState
    private val defaultMarkerId = UUID.randomUUID().toString()

    private val locationComponentOptions = LocationComponentOptions.builder(context)
        .pulseEnabled(true)
        .build()
    private val locationActivationOptions = LocationComponentActivationOptions.builder(context, style)
        .useDefaultLocationEngine(true)
        .locationComponentOptions(locationComponentOptions)
        .build()

    private val positionTrackingState by lazy {
        map.locationComponent.activateLocationComponent(locationActivationOptions)
        MutableStateFlow(map.locationComponent.isLocationComponentEnabled)
    }

    val trackingState: Flow<Boolean> = positionTrackingState

    private val symbolManager = SymbolManager(view, map, style).also {
        it.iconAllowOverlap = true
        it.addClickListener {
            val json = (it.data as JsonObject)
            map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(json.get("lat").asDouble, json.get("lon").asDouble)))
            selectedIdState.value = json.get("id").asLong
            true
        }
        map.addOnMapClickListener {
            continuePropagation { selectedIdState.value = null }
        }
        style.addImage(defaultMarkerId, BitmapFactory.decodeResource(resources, R.drawable.symbol_14))
    }

    fun showMarkers(markers: Collection<MarkerViewModel>) {
        markers
            .map { marker ->
                SymbolOptions()
                    .withData(JsonObject().also {
                        it.addProperty("id", marker.id)
                        it.addProperty("lat", marker.coordinates.latitude.asDecimal)
                        it.addProperty("lon", marker.coordinates.longitude.asDecimal)
                    })
                    .withLatLng(LatLng(marker.coordinates.latitude.asDecimal, marker.coordinates.longitude.asDecimal))
                    .withIconImage(marker.symbol?.let { createImage(it, style) } ?: defaultMarkerId)
            }
            .run { symbolManager.create(this) }
    }

    fun zoomTo(coordinates: GeoCoordinates, zoom: Double) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(coordinates.latitude.asDecimal, coordinates.longitude.asDecimal),
            zoom
        ))
    }

    @RequiresPermission(anyOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
    fun enablePositionTracking() {
        positionTrackingState.value = true
        map.locationComponent.isLocationComponentEnabled = true
        map.locationComponent.setCameraMode(CameraMode.TRACKING_GPS, object: OnLocationCameraTransitionListener {
            override fun onLocationCameraTransitionFinished(cameraMode: Int) {
                map.locationComponent.zoomWhileTracking(ZoomLevels.ROADS)
            }
            override fun onLocationCameraTransitionCanceled(cameraMode: Int) {}
        })
    }

    @SuppressLint("MissingPermission")
    fun disablePositionTracking() {
        positionTrackingState.value = false
        map.locationComponent.isLocationComponentEnabled = false
    }

    private fun createImage(image: Bitmap, style: Style): String {
        val id = UUID.randomUUID().toString()

        style.addImage(id, image)

        return id
    }
}
