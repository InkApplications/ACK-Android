package com.inkapplications.aprs.android.capture.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.inkapplications.aprs.android.capture.log.LogItemState
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.aprs.android.map.lifecycleObserver
import com.inkapplications.aprs.android.station.startStationActivity
import com.inkapplications.coroutines.collectOn
import com.mapbox.mapboxsdk.maps.MapView
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kotlinx.android.synthetic.main.map.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

private const val LOCATION_REQUEST = 3684

class MapFragment: Fragment() {
    private lateinit var mapEventsFactory: MapEventsFactory
    private val mapViewModel = MutableSharedFlow<MapViewModel>()
    private var mapView: MapView? = null
    private var map: Map? = null
    private var mapScope: CoroutineScope = MainScope()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mapEventsFactory = component.mapManager()

        return ComposeView(context!!).apply {
            setContent {
                val viewState = mapViewModel.collectAsState(MapViewModel(emptySet(), null, false))

                MapScreen(
                    state = viewState.value,
                    mapFactory = ::createMapView,
                    onLogItemClick = ::onLogItemClick,
                    onEnableLocation = ::enablePositionTracking,
                    onDisableLocation = { map?.disablePositionTracking() },
                )
            }
        }
    }

    private fun createMapView(context: Context): View = MapView(context).also { mapView ->
        this.mapView = mapView

        mapView.getMap(activity!!, ::onMapLoaded)
        lifecycle.addObserver(mapView.lifecycleObserver)

        return mapView
    }

    private fun onMapLoaded(map: Map) {
        this.map = map
        mapScope.cancel()
        mapScope = MainScope()
        val manager = mapEventsFactory.createEventsAccess(map)

        manager.viewState.collectOn(mapScope) { state ->
            Kimchi.trackEvent("map_markers", listOf(intProperty("quantity", state.markers.size)))
            mapViewModel.emit(state)
            map.showMarkers(state.markers)

        }
    }

    private fun onLogItemClick(state: LogItemState) {
        activity!!.startStationActivity(state.id)
    }

    private fun enablePositionTracking() {
        when(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> map?.enablePositionTracking()
            else -> requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when {
            requestCode == LOCATION_REQUEST && grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED -> {
                Kimchi.trackEvent("location_permission_grant")
                enablePositionTracking()
            }
            requestCode == LOCATION_REQUEST -> {
                Kimchi.trackEvent("location_permission_deny")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroyView() {
        mapScope.cancel()
        map = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }
}
