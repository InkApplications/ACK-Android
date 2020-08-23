package com.inkapplications.aprs.android.capture.map

import android.graphics.Bitmap
import com.google.gson.JsonObject
import com.inkapplications.android.extensions.continuePropagation
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
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
    private val view: MapView,
    private val map: MapboxMap,
    private val style: Style
) {
    private val selectedIdState = MutableStateFlow<Long?>(null)
    val selectedId = selectedIdState

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
                    .withIconImage(createImage(marker.symbol, style))
            }
            .run { symbolManager.create(this) }
    }

    private fun createImage(image: Bitmap, style: Style): String {
        val id = UUID.randomUUID().toString()

        style.addImage(id, image)

        return id
    }
}

