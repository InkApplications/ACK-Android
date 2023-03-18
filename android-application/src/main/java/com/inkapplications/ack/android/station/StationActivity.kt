package com.inkapplications.ack.android.station

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.log.details.startLogInspectActivity
import com.inkapplications.ack.android.map.MapController
import com.inkapplications.ack.android.map.mapbox.createController
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import com.inkapplications.coroutines.collectOn
import com.mapbox.maps.MapView
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import javax.inject.Inject

private const val EXTRA_CALLSIGN = "aprs.station.extra.callsign"

@AndroidEntryPoint
class StationActivity: ExtendedActivity(), StationScreenController {
    @Inject
    lateinit var stationEvents: StationEvents

    private var mapView: MapView? = null

    private val callsign get() = intent.getStringExtra(EXTRA_CALLSIGN)!!.let(::Callsign)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewState = stationEvents.stationState(callsign).collectAsState(StationViewState.Initial)
            AckScreen {
                StationScreen(
                    viewState = viewState.value,
                    createMapView = ::createMapView,
                    controller = this,
                )
            }
        }
    }

    private fun createMapView(context: Context) = mapView ?: MapView(context).also { mapView ->
        this.mapView = mapView

        mapView.createController(this, ::onMapLoaded, ::onMapItemClicked)
    }

    private fun onMapLoaded(map: MapController) {
        stationEvents.stationState(callsign).collectOn(foregroundScope) { viewModel ->
            if (viewModel is StationViewState.Loaded) {
                map.setCamera(viewModel.insight.mapCameraPosition)
                map.showMarkers(viewModel.insight.markers)
            }
        }
    }

    private fun onMapItemClicked(id: Long?) {
        Kimchi.debug("Map Item Clicked: No-Op")
    }

    override fun onLogItemClicked(item: LogItemViewState) {
        startLogInspectActivity(item.id)
    }
}

fun Activity.startStationActivity(callsign: Callsign) {
    Kimchi.trackNavigation("station")
    startActivity(StationActivity::class) {
        putExtra(EXTRA_CALLSIGN, callsign.canonical)
    }
}
