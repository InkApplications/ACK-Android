package com.inkapplications.ack.android.map.mapbox

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.MapController
import com.inkapplications.ack.android.map.MarkerViewState
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

/**
 * Create a Map controller by initializing a Mapbox Map.
 */
inline fun MapView.createController(
    activity: Context,
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

@Composable
fun MarkerMap(
    markers: Collection<MarkerViewState>,
    cameraPosition: MapCameraPosition,
    onMapItemClicked: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var controllerState by remember { mutableStateOf<MapController?>(null) }
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                createController(
                    activity = context,
                    onInit = { controller ->
                        controllerState = controller
                        controller.setCamera(cameraPosition)
                        controller.showMarkers(markers)
                    },
                    onSelect = onMapItemClicked,
                )
            }
        },
        update = { mapView ->
            controllerState?.let { controller ->
                controller.setCamera(cameraPosition)
                controller.showMarkers(markers)
            }
        },
        modifier = modifier,
    )
}
