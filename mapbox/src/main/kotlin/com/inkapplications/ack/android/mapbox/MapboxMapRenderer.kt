package com.inkapplications.ack.android.mapbox

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.ack.android.maps.MapController
import com.inkapplications.ack.android.maps.MapRenderer
import com.inkapplications.ack.android.maps.MapViewModel
import com.inkapplications.ack.data.CaptureId
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import kotlinx.coroutines.delay

object MapboxMapRenderer: MapRenderer {
    @SuppressLint("MissingPermission")
    @Composable
    override fun renderMarkerMap(
        viewModel: MapViewModel,
        onMapItemClicked: (CaptureId?) -> Unit,
        bottomProtection: Dp,
        interactive: Boolean,
        modifier: Modifier
    ) {
        var controllerState by remember { mutableStateOf<MapController?>(null) }
        val bottomProtectionPx = with(LocalDensity.current) {
            bottomProtection.toPx()
        }
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    createController(
                        activity = context,
                        onInit = { controller ->
                            controllerState = controller
                            controller.setCamera(viewModel.cameraPosition)
                            controller.showMarkers(viewModel.markers)
                            controller.setBottomPadding(bottomProtectionPx)
                            controller.setPanEnabled(interactive)
                            if (viewModel.enablePositionTracking) {
                                controller.enablePositionTracking()
                            } else {
                                controller.disablePositionTracking()
                            }
                        },
                        onSelect = onMapItemClicked,
                    )
                }
            },
            update = { mapView ->
                controllerState?.let { controller ->
                    controller.setCamera(viewModel.cameraPosition)
                    controller.showMarkers(viewModel.markers)
                    controller.setBottomPadding(bottomProtectionPx)
                    controller.setPanEnabled(interactive)
                    if (viewModel.enablePositionTracking) {
                        controller.enablePositionTracking()
                    } else {
                        controller.disablePositionTracking()
                    }
                }
            },
            modifier = modifier,
        )
    }

    override suspend fun initialize(application: Application) {
        ResourceOptionsManager.getDefault(application, BuildConfig.MAPBOX_ACCESS_TOKEN)
        delay(500) // Fix race condition in MapView initialization
    }
}
