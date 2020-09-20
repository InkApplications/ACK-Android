package com.inkapplications.aprs.android.capture.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.inkapplications.android.extensions.setVisibility
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.aprs.android.map.lifecycleObserver
import com.inkapplications.aprs.android.station.startStationActivity
import com.inkapplications.coroutines.collectOn
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kotlinx.android.synthetic.main.map.*
import kotlinx.coroutines.*

private const val LOCATION_REQUEST = 3684

class MapFragment: Fragment() {
    private lateinit var mapEventsFactory: MapEventsFactory
    private var map: Map? = null
    private var mapScope: CoroutineScope = MainScope()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapEventsFactory = component.mapManager()
        lifecycle.addObserver(map_view.lifecycleObserver)

        map_position_enabled.setOnClickListener { map?.disablePositionTracking() }
        map_position_disabled.setOnClickListener { enablePositionTracking() }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        map_view.getMap(activity!!, ::onMapLoaded)
    }

    override fun onStart() {
        super.onStart()
        map_view.getMap(activity!!, ::onMapLoaded)
    }

    private fun onMapLoaded(map: Map) {
        this.map = map
        mapScope.cancel()
        mapScope = MainScope()
        val manager = mapEventsFactory.createEventsAccess(map)

        manager.viewState.collectOn(mapScope) { state ->
            map_position_enabled.setVisibility(state.positionEnabledVisible)
            map_position_disabled.setVisibility(state.positionDisabledVisible)
            map_selected.setVisibility(state.selectedItemVisible)
            state.selectedItem?.bindToView(map_selected)
            map_selected.setOnClickListener {
                activity!!.startStationActivity(state.selectedItem!!.id)
            }
            Kimchi.trackEvent("map_markers", listOf(intProperty("quantity", state.markers.size)))
            map.showMarkers(state.markers)

        }
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

    override fun onStop() {
        mapScope.cancel()
        map = null
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }
}
