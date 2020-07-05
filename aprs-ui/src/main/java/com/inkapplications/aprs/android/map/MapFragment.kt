package com.inkapplications.aprs.android.map

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.libraries.maps.SupportMapFragment
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.kotlin.collectOn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MapFragment: Fragment() {
    private lateinit var mapData: MapDataRepository
    private lateinit var foreground: CoroutineScope
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapData = component.mapData()
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            map.configure(activity!!)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        mapFragment.getMapAsync { map -> map.configure(activity!!) }
    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()

        mapData.findMarkers().collectOn(foreground) { packets ->
            mapFragment.getMapAsync { map ->
                map.clear()
                packets.forEach { map.addMarker(it) }
            }
        }
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}
