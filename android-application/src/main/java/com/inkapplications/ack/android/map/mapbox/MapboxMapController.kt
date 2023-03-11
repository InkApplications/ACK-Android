package com.inkapplications.ack.android.map.mapbox

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.RequiresPermission
import com.google.gson.JsonObject
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.map.*
import com.inkapplications.android.continuePropagation
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport
import java.util.*

/**
 * Adapt a Mapbox Map view into our controller interface.
 */
class MapboxMapController(
    private val view: MapView,
    private val map: MapboxMap,
    private val style: Style,
    private val resources: Resources,
    private val onSelect: (Long?) -> Unit,
): MapController {
    private val defaultMarkerId = UUID.randomUUID().toString()

    private val symbolManager = view.annotations.createPointAnnotationManager().also {
        it.iconAllowOverlap = true
        it.addClickListener {
            val json = (it.getData() as JsonObject)
            map.easeTo(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(json.get("lon").asDouble, json.get("lat").asDouble))
                    .build()
            )
            onSelect(json.get("id").asLong)
            true
        }
        map.addOnMapClickListener {
            continuePropagation { onSelect(null) }
        }
        style.addImage(defaultMarkerId, BitmapFactory.decodeResource(resources, R.drawable.symbol_14))
    }

    override fun initDefaults() {
        view.scalebar.enabled = false
        setCamera(CameraPositionDefaults.unknownLocation)
        view.location.updateSettings {
            pulsingEnabled = true
        }
    }

    override fun setBottomPadding(padding: Float) {
        view.attribution.marginBottom = padding
        view.logo.marginBottom = padding
    }

    override fun showMarkers(markers: Collection<MarkerViewState>) {
        markers
            .map { marker ->
                PointAnnotationOptions()
                    .withData(JsonObject().also {
                        it.addProperty("id", marker.id)
                        it.addProperty("lat", marker.coordinates.latitude.asDecimal)
                        it.addProperty("lon", marker.coordinates.longitude.asDecimal)
                    })
                    .withPoint(Point.fromLngLat(marker.coordinates.longitude.asDecimal, marker.coordinates.latitude.asDecimal))
                    .withIconImage(marker.symbol?.let { createImage(it, style) } ?: defaultMarkerId)
            }
            .run { symbolManager.create(this) }
    }

    override fun zoomTo(cameraPosition: MapCameraPosition) {
        map.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(cameraPosition.coordinates.longitude.asDecimal, cameraPosition.coordinates.latitude.asDecimal))
                .zoom(cameraPosition.zoom.mapboxValue)
                .build()
        )
    }

    override fun setCamera(cameraPosition: MapCameraPosition) {
        map.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(cameraPosition.coordinates.longitude.asDecimal, cameraPosition.coordinates.latitude.asDecimal))
                .zoom(cameraPosition.zoom.mapboxValue)
                .build()
        )
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun enablePositionTracking() {
        view.location.enabled = true
        val state = view.viewport.makeFollowPuckViewportState(
            FollowPuckViewportStateOptions.Builder()
                .zoom(ZoomLevels.ROADS.mapboxValue)
                .pitch(0.0)
                .bearing(FollowPuckViewportStateBearing.Constant(0.0))
                .build()
        )
        view.viewport.transitionTo(state)
    }

    @SuppressLint("MissingPermission")
    override fun disablePositionTracking() {
        view.location.enabled = false
    }

    private fun createImage(image: Bitmap, style: Style): String {
        val id = UUID.randomUUID().toString()

        style.addImage(id, image)

        return id
    }
}
