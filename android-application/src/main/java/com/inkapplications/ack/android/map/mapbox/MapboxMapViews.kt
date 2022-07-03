package com.inkapplications.ack.android.map.mapbox

import android.app.Activity
import android.content.res.Configuration
import com.inkapplications.ack.android.map.MapController
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

/**
 * Create a Map controller by initializing a Mapbox Map.
 */
inline fun MapView.createController(
    activity: Activity,
    crossinline onInit: (MapController) -> Unit,
    noinline onSelect: (Long?) -> Unit,
) {
    val map = getMapboxMap()
    val styleUri = if (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
        Style.DARK
    } else {
        Style.LIGHT
    }

    map.loadStyleUri(styleUri) { style ->
        val controller = MapboxMapController(this, map, style, activity.resources, onSelect = onSelect)
        controller.initDefaults()
        onInit(controller)
    }
}
