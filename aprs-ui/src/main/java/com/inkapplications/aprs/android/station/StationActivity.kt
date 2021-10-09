package com.inkapplications.aprs.android.station

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.aprs.android.map.lifecycleObserver
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.trackNavigation
import com.inkapplications.aprs.android.ui.theme.AprsScreen
import com.inkapplications.coroutines.collectOn
import com.mapbox.mapboxsdk.maps.MapView
import kimchi.Kimchi

private const val EXTRA_ID = "aprs.station.extra.id"

class StationActivity: ExtendedActivity() {
    private var mapView: MapView? = null
    private lateinit var stationEvents: StationEvents

    private val id get() = intent.getLongExtra(EXTRA_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stationEvents = component.stationEvents()

        setContent {
            val viewState = stationEvents.stateEvents(id).collectAsState(StationViewModel())
            AprsScreen {
                StationScreen(
                    viewState = viewState.value,
                    createMapView = ::createMapView,
                    onBackPressed = ::onBackPressed,
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
        stationEvents.stateEvents(id).collectOn(foregroundScope) { viewModel ->
            map.zoomTo(viewModel.center, viewModel.zoom)
            map.showMarkers(viewModel.markers)
        }
    }
}

fun Activity.startStationActivity(stationId: Long) {
    Kimchi.trackNavigation("station")
    startActivity(StationActivity::class) {
        putExtra(EXTRA_ID, stationId)
    }
}
