package com.inkapplications.ack.android.map.mapbox

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.RequiresPermission
import com.google.gson.JsonObject
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.map.*
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
import java.util.*

/**
 * Adapt a Mapbox Map view into our controller interface.
 */
class MapboxMapController(
    private val context: Context,
    private val view: MapView,
    private val map: MapboxMap,
    private val style: Style,
    private val resources: Resources,
    private val onSelect: (Long?) -> Unit,
): MapController {
    private val defaultMarkerId = UUID.randomUUID().toString()

    private val locationComponentOptions = LocationComponentOptions.builder(context)
        .pulseEnabled(true)
        .build()
    private val locationActivationOptions = LocationComponentActivationOptions.builder(context, style)
        .useDefaultLocationEngine(true)
        .locationComponentOptions(locationComponentOptions)
        .build()

    private val symbolManager = SymbolManager(view, map, style).also {
        it.iconAllowOverlap = true
        it.addClickListener {
            val json = (it.data as JsonObject)
            map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(json.get("lat").asDouble, json.get("lon").asDouble)))
            onSelect(json.get("id").asLong)
            true
        }
        map.addOnMapClickListener {
            continuePropagation { onSelect(null) }
        }
        style.addImage(defaultMarkerId, BitmapFactory.decodeResource(resources, R.drawable.symbol_14))
    }

    override fun initDefaults() {
        map.uiSettings.run {
            setLogoMargins(logoMarginLeft, logoMarginTop, logoMarginRight, context.resources.getDimensionPixelSize(R.dimen.mapbox_logo_padding_bottom))
        }
        setCamera(CameraPositionDefaults.unknownLocation)
        map.locationComponent.activateLocationComponent(locationActivationOptions)
    }

    override fun showMarkers(markers: Collection<MarkerViewModel>) {
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

    override fun zoomTo(cameraPosition: MapCameraPosition) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(cameraPosition.coordinates.latitude.asDecimal, cameraPosition.coordinates.longitude.asDecimal),
                cameraPosition.zoom.mapboxValue
            )
        )
    }

    override fun setCamera(cameraPosition: MapCameraPosition) {
        map.cameraPosition = com.mapbox.mapboxsdk.camera.CameraPosition.Builder()
            .zoom(cameraPosition.zoom.mapboxValue)
            .target(
                LatLng(
                    cameraPosition.coordinates.latitude.asDecimal,
                    cameraPosition.coordinates.longitude.asDecimal
                )
            )
            .build()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun enablePositionTracking() {
        map.locationComponent.isLocationComponentEnabled = true
        map.locationComponent.setCameraMode(CameraMode.TRACKING_GPS, object: OnLocationCameraTransitionListener {
            override fun onLocationCameraTransitionFinished(cameraMode: Int) {
                map.locationComponent.zoomWhileTracking(ZoomLevels.ROADS.mapboxValue)
            }
            override fun onLocationCameraTransitionCanceled(cameraMode: Int) {}
        })
    }

    @SuppressLint("MissingPermission")
    override fun disablePositionTracking() {
        map.locationComponent.isLocationComponentEnabled = false
    }

    private fun createImage(image: Bitmap, style: Style): String {
        val id = UUID.randomUUID().toString()

        style.addImage(id, image)

        return id
    }
}
