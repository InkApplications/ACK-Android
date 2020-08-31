package com.inkapplications.aprs.android.station

import android.app.Activity
import android.os.Bundle
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.setVisibility
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.map.getMap
import com.inkapplications.kotlin.collectOn
import kotlinx.android.synthetic.main.station.*

private const val EXTRA_ID = "aprs.station.extra.id"

class StationActivity: ExtendedActivity() {
    private lateinit var stationEvents: StationEvents

    private val id get() = intent.getLongExtra(EXTRA_ID, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.station)

        stationEvents = component.stationEvents()
    }

    override fun onStart() {
        super.onStart()

        station_map.getMap(this) { map ->
            stationEvents.stateEvents(id).collectOn(foregroundScope) { viewModel ->
                map.showMarkers(viewModel.markers)
                station_map.setVisibility(viewModel.mapVisible)
                station_name.text = viewModel.name
                station_comment.text = viewModel.comment
            }
        }
    }
}

fun Activity.startStationActivity(stationId: Long) {
    startActivity(StationActivity::class) {
        putExtra(EXTRA_ID, stationId)
    }
}
