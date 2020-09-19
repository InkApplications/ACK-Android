package com.inkapplications.aprs.android.map

import android.Manifest.permission
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.RequiresPermission
import com.google.gson.JsonObject
import com.inkapplications.android.extensions.continuePropagation
import com.inkapplications.aprs.android.R
import com.inkapplications.karps.structures.unit.Coordinates
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
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
                        it.addProperty("lat", marker.coordinates.latitude.decimal)
                        it.addProperty("lon", marker.coordinates.longitude.decimal)
                    })
                    .withLatLng(LatLng(marker.coordinates.latitude.decimal, marker.coordinates.longitude.decimal))
                    .withIconImage(marker.symbol?.let { createImage(it, style) } ?: defaultMarkerId)
            }
            .run { symbolManager.create(this) }
    }

    fun zoomTo(coordinates: Coordinates, zoom: Double) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(coordinates.latitude.decimal, coordinates.longitude.decimal),
            zoom
        ))
    }

    @RequiresPermission(anyOf = [permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION])
    fun setPositionTracking(enable: Boolean) {
        map.locationComponent.apply {
            activateLocationComponent(locationActivationOptions)
            isLocationComponentEnabled = enable
        }
    }

    private fun createImage(image: Bitmap, style: Style): String {
        val id = UUID.randomUUID().toString()

        style.addImage(id, image)

        return id
    }
}
