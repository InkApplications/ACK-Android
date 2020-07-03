package com.inkapplications.aprs.android.map

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MapStyleOptions
import com.google.android.libraries.maps.model.MarkerOptions
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.AprsPacket
import com.inkapplications.kotlin.collectOn
import kimchi.Kimchi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MapFragment: Fragment() {
    private lateinit var aprs: AprsAccess
    private lateinit var foreground: CoroutineScope
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        aprs = component.aprs()
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

        aprs.data.collectOn(foreground) {
            Kimchi.debug("New APRS Data received.")
        }
        aprs.findRecent(50)
            .collectOn(foreground) { packets ->
                mapFragment.getMapAsync { map ->
                    map.clear()
                    packets.forEach { packet ->
                        when (packet) {
                            is AprsPacket.Location -> {
                                map.addMarker(MarkerOptions().apply {
                                    position(LatLng(packet.position.latitude, packet.position.longitude))
                                    title(packet.comment)
                                })
                            }
                        }
                    }
                }
            }
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}

fun GoogleMap.configure(activity: Activity) {
    if (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_dark)
        )
    } else {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_light)
        )
    }
}
