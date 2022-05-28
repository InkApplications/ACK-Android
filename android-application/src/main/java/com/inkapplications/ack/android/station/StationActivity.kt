package com.inkapplications.ack.android.station

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.ack.android.component
import com.inkapplications.ack.android.log.LogItemViewModel
import com.inkapplications.ack.android.log.details.startLogInspectActivity
import com.inkapplications.ack.android.map.Map
import com.inkapplications.ack.android.map.getMap
import com.inkapplications.ack.android.map.lifecycleObserver
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import com.inkapplications.coroutines.collectOn
import com.mapbox.mapboxsdk.maps.MapView
import kimchi.Kimchi

private const val EXTRA_CALLSIGN = "aprs.station.extra.callsign"

class StationActivity: ExtendedActivity(), StationScreenController {
    private var mapView: MapView? = null
    private lateinit var stationEvents: StationEvents

    private val callsign get() = intent.getStringExtra(EXTRA_CALLSIGN)!!.let(::Callsign)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stationEvents = component.stationEvents()

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

        mapView.getMap(this, ::onMapLoaded)
        lifecycle.addObserver(mapView.lifecycleObserver)
    }

    private fun onMapLoaded(map: Map) {
        stationEvents.stationState(callsign).collectOn(foregroundScope) { viewModel ->
            if (viewModel is StationViewState.Loaded) {
                map.zoomTo(viewModel.insight.mapCameraPosition)
                map.showMarkers(viewModel.insight.markers)
            }
        }
    }

    override fun onLogItemClicked(item: LogItemViewModel) {
        startLogInspectActivity(item.id)
    }
}

fun Activity.startStationActivity(callsign: Callsign) {
    Kimchi.trackNavigation("station")
    startActivity(StationActivity::class) {
        putExtra(EXTRA_CALLSIGN, callsign.canonical)
    }
}
