package com.inkapplications.aprs.android.map

import android.app.Activity
import android.content.res.Configuration
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

/**
 * Set the mapbox theme based on Android's light/dark mode.
 */
inline fun MapView.init(activity: Activity, crossinline onInit: (MapboxMap, Style) -> Unit = { _, _ -> }) {
    getMapAsync { map ->
        val theme = if (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            Style.DARK
        } else {
            Style.LIGHT
        }

        map.setStyle(theme) { style ->
            onInit(map, style)
        }
    }
}

/**
 * Get a Map object by initializing a Mapbox Map.
 */
inline fun MapView.getMap(activity: Activity, crossinline onInit: (Map) -> Unit) {
    init(activity) { mapbox, style ->
        val map = Map(this, mapbox, style)
        onInit(map)
    }
}
