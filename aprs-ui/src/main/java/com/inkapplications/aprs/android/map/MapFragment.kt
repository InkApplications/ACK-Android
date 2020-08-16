package com.inkapplications.aprs.android.map

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inkapplications.android.extensions.setVisibility
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.kotlin.collectOn
import kotlinx.android.synthetic.main.map.*
import kotlinx.coroutines.*

class MapFragment: Fragment() {
    private lateinit var mapManagerFactory: MapManagerFactory
    private lateinit var foreground: CoroutineScope
    private var mapJobs = Job()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapManagerFactory = component.mapManager()
        lifecycle.addObserver(map_view.lifecycleObserver)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initializeMap()
    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()
        initializeMap()
    }

    private fun initializeMap() {
        map_view.init(activity!!) { map, style ->
            mapJobs.cancel()
            mapJobs = Job()
            val manager = mapManagerFactory.create(map_view, map, style)

            manager.selectionState.collectOn(foreground + mapJobs) { state ->
                map_selected.setVisibility(state.visible)
                state.item?.bindToView(map_selected)
            }
            foreground.launch(mapJobs) { manager.displayMarkers() }
        }
    }

    override fun onStop() {
        foreground.cancel()
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
