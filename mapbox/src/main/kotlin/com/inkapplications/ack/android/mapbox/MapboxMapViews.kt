package com.inkapplications.ack.android.mapbox

import android.content.Context
import android.content.res.Configuration
import com.inkapplications.ack.android.maps.MapController
import com.inkapplications.ack.data.CaptureId
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

/**
 * Create a Map controller by initializing a Mapbox Map.
 */
internal inline fun MapView.createController(
    activity: Context,
    crossinline onInit: (MapController) -> Unit,
    noinline onSelect: (CaptureId?) -> Unit,
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
