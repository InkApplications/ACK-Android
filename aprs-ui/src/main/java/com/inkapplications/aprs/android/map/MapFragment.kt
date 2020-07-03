package com.inkapplications.aprs.android.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.AprsPacket
import com.inkapplications.kotlin.collectOn
import kimchi.Kimchi
import kotlinx.android.synthetic.main.map.*
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
            CameraUpdateFactory.newLatLngZoom(LatLng(45.0499576,-93.2584823), 10f).run {
                map.moveCamera(this)
            }

            map.addMarker(MarkerOptions().apply {
                position(LatLng(45.0499576,-93.2584823))
                title("Hello Wald")
            })
        }

    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()

        aprs.data.collectOn(foreground) { packet ->
            Kimchi.info("Packet: $packet")
            when (packet) {
                is AprsPacket.Location -> {
                    mapFragment.getMapAsync { map ->
                        map.addMarker(MarkerOptions().apply {
                            position(LatLng(packet.position.latitude, packet.position.longitude))
                            title(packet.comment)
                        })
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
