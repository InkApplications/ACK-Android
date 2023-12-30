package com.inkapplications.ack.android.station

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.log.details.startLogInspectActivity
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import javax.inject.Inject

const val EXTRA_CALLSIGN = "aprs.station.extra.callsign"

/**
 * Displays information and recent packets for a particular station.
 */
@AndroidEntryPoint
class StationActivity: ExtendedActivity(), StationScreenController {
    @Inject
    lateinit var stationEvents: StationEvents

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Kimchi.trackScreen("station")

        setContent {
            AckScreen {
                StationScreen(
                    controller = this,
                )
            }
        }
    }

    override fun onMapItemClicked(id: CaptureId?) {
        Kimchi.trackEvent("station_map_item_click")
        Kimchi.debug("Map Item Clicked: No-Op")
    }

    override fun onLogItemClicked(item: LogItemViewState) {
        Kimchi.trackEvent("station_log_item_click")
        startLogInspectActivity(item.id)
    }
}

/**
 * Start the station activity for the station with the given callsign.
 *
 * @param callsign The callsign of the station to display.
 */
fun Activity.startStationActivity(callsign: Callsign) {
    Kimchi.trackNavigation("station")
    startActivity(StationActivity::class) {
        putExtra(EXTRA_CALLSIGN, callsign.canonical)
    }
}
