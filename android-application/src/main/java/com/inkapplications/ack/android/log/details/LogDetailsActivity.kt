package com.inkapplications.ack.android.log.details

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.inkapplications.ack.android.log.LogEvents
import com.inkapplications.ack.android.map.MapController
import com.inkapplications.ack.android.map.mapbox.createController
import com.inkapplications.ack.android.station.startStationActivity
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import com.mapbox.maps.MapView
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

const val EXTRA_LOG_ID = "aprs.station.extra.id"

/**
 * Shows information about a particular packet received.
 */
@AndroidEntryPoint
class LogDetailsActivity: ExtendedActivity(), LogDetailsController {
    @Inject
    lateinit var logEvents: LogEvents

    private var mapView: MapView? = null

    private val id get() = intent.getLongExtra(EXTRA_LOG_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AckScreen {
                LogDetailsScreen(
                    controller = this,
                    mapViewFactory = ::createMapView,
                )
            }
        }
    }

    private fun createMapView(context: Context) = mapView ?: MapView(context).also { mapView ->
        this.mapView = mapView

        mapView.createController(this, ::onMapLoaded, ::onMapItemClicked)
    }

    private fun onMapLoaded(map: MapController) {
        Kimchi.trace("Map Loaded")
        lifecycleScope.launchWhenCreated {
            logEvents.mapState(id).collectLatest { state ->
                map.setCamera(state.mapCameraPosition)
                map.showMarkers(state.markers)
            }
        }
    }

    private fun onMapItemClicked(id: Long?) {
        Kimchi.debug("Map Item Clicked: No-Op")
    }

    override fun onViewStationDetails(station: Callsign) {
        startStationActivity(station)
    }
}

/**
 * Start an Activity displaying the details for the specified packet.
 */
fun Activity.startLogInspectActivity(packetId: Long) {
    Kimchi.trackNavigation("log-inspect")
    startActivity(LogDetailsActivity::class) {
        putExtra(EXTRA_LOG_ID, packetId)
    }
}
