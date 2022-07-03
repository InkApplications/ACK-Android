package com.inkapplications.ack.android.log.details

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.ack.android.log.LogEvents
import com.inkapplications.ack.android.component
import com.inkapplications.ack.android.map.MapController
import com.inkapplications.ack.android.map.mapbox.createController
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import com.inkapplications.coroutines.collectOn
import com.mapbox.maps.MapView
import kimchi.Kimchi

private const val EXTRA_ID = "aprs.station.extra.id"

/**
 * Shows information about a particular packet received.
 */
class LogDetailsActivity: ExtendedActivity(), LogDetailsController {
    private var mapView: MapView? = null
    private lateinit var logEvents: LogEvents

    private val id get() = intent.getLongExtra(EXTRA_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEvents = component.logData()

        setContent {
            val viewState = logEvents.stateEvents(id).collectAsState(LogDetailsState.Initial)
            AckScreen {
                LogDetailsScreen(
                    viewState = viewState.value,
                    controller = this,
                    createMapView = ::createMapView,
                )
            }
        }
    }

    private fun createMapView(context: Context) = mapView ?: MapView(context).also { mapView ->
        this.mapView = mapView

        mapView.createController(this, ::onMapLoaded, ::onMapItemClicked)
    }

    private fun onMapLoaded(map: MapController) {
        logEvents.stateEvents(id).collectOn(foregroundScope) { viewModel ->
            if (viewModel is LogDetailsState.LogDetailsViewModel) {
                map.setCamera(viewModel.mapCameraPosition)
                map.showMarkers(viewModel.markers)
            }
        }
    }

    private fun onMapItemClicked(id: Long?) {
        Kimchi.debug("Map Item Clicked: No-Op")
    }
}

fun Activity.startLogInspectActivity(packetId: Long) {
    Kimchi.trackNavigation("log-inspect")
    startActivity(LogDetailsActivity::class) {
        putExtra(EXTRA_ID, packetId)
    }
}
