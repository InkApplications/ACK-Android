package com.inkapplications.aprs.android.capture.map

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inkapplications.android.extensions.setVisibility
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.aprs.android.map.lifecycleObserver
import com.inkapplications.aprs.android.station.startStationActivity
import com.inkapplications.kotlin.collectOn
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kotlinx.android.synthetic.main.map.*
import kotlinx.coroutines.*

class MapFragment: Fragment() {
    private lateinit var mapManager: MapManager
    private var mapScope: CoroutineScope = MainScope()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapManager = component.mapManager()
        lifecycle.addObserver(map_view.lifecycleObserver)

        map_position_enabled.setOnClickListener { mapManager.disablePositionTracking() }
        map_position_disabled.setOnClickListener { mapManager.enablePositionTracking() }
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
        mapScope.cancel()
        mapScope = MainScope()
        val manager = mapManager.createEventsAccess(map)

        manager.viewState.collectOn(mapScope) { state ->
            map.setPositionTracking(state.trackPosition)
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

    override fun onStop() {
        mapScope.cancel()
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
