package com.inkapplications.ack.android.log.details

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.ack.android.station.startStationActivity
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi

const val EXTRA_LOG_ID = "aprs.station.extra.id"

/**
 * Shows information about a particular packet received.
 */
@AndroidEntryPoint
class LogDetailsActivity: ExtendedActivity(), LogDetailsController {
    private val id get() = CaptureId(intent.getLongExtra(EXTRA_LOG_ID, -1))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Kimchi.trackScreen("log_details")

        setContent {
            AckScreen {
                LogDetailsScreen(
                    controller = this,
                )
            }
        }
    }

    override fun onMapItemClicked(id: CaptureId?) {
        Kimchi.trackEvent("log_details_map_item_click")
        Kimchi.debug("Map Item Clicked: No-Op")
    }

    override fun onViewStationDetails(station: Callsign) {
        Kimchi.trackEvent("log_details_view_station")
        startStationActivity(station)
    }
}

/**
 * Start an Activity displaying the details for the specified packet.
 */
fun Activity.startLogInspectActivity(packetId: CaptureId) {
    Kimchi.trackNavigation("log_inspect")
    startActivity(LogDetailsActivity::class) {
        putExtra(EXTRA_LOG_ID, packetId.value)
    }
}
