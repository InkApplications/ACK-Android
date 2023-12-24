package com.inkapplications.ack.android.capture

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.log.index.LogIndexController
import com.inkapplications.ack.android.log.details.startLogInspectActivity
import com.inkapplications.ack.android.capture.messages.index.MessagesScreenController
import com.inkapplications.ack.android.capture.messages.conversation.startConversationActivity
import com.inkapplications.ack.android.capture.messages.create.CreateConversationActivity
import com.inkapplications.ack.android.capture.service.BackgroundCaptureService
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.map.*
import com.inkapplications.ack.android.map.mapbox.createController
import com.inkapplications.ack.android.settings.SettingsActivity
import com.inkapplications.ack.android.station.startStationActivity
import com.inkapplications.ack.android.tnc.startConnectTncActivity
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.PermissionGate
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import com.inkapplications.coroutines.collectOn
import com.mapbox.maps.MapView
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kimchi.analytics.stringProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Capture controls and data exploration.
 *
 * This is the primary activity in the application and provides controls
 * to capture APRS packets and explore the data that has been captured.
 */
@AndroidEntryPoint
class CaptureActivity: ExtendedActivity(), CaptureNavController, LogIndexController {
    @Inject
    lateinit var mapEvents: MapEvents

    @Inject
    lateinit var captureEvents: CaptureEvents

    private var mapView: MapView? = null
    private var mapScope: CoroutineScope = MainScope()
    private val mapViewState = MutableStateFlow(MapViewState())
    private val permissionGate = PermissionGate(this)
    private val backgroundCaptureServiceIntent by lazy { Intent(this, BackgroundCaptureService::class.java) }

    override fun onCreate() {
        super.onCreate()

        setContent {
            val mapState = mapViewState.collectAsState()
            val messagesScreenController = object: MessagesScreenController {
                override fun onCreateMessageClick() {
                    startActivity(CreateConversationActivity::class)
                }
                override fun onConversationClick(callsign: Callsign) {
                    startConversationActivity(callsign)
                }
            }

            CaptureScreen(
                mapState = mapState,
                logIndexController = this,
                messagesScreenController = messagesScreenController,
                mapFactory = ::createMapView,
                controller = this,
            )
        }
    }

    private fun createMapView(context: Context): View {
        return if(mapView != null) mapView!! else  MapView(context).also { mapView ->
            this.mapView = mapView

            mapView.createController(this, ::onMapLoaded, ::onMapItemSelected)

            return mapView
        }
    }

    private fun onMapItemSelected(id: Long?) {
        mapEvents.selectedItemId.value = id
    }

    private fun onMapLoaded(map: MapController) {
        mapScope.cancel()
        mapScope = MainScope()

        map.setBottomPadding(resources.getDimension(R.dimen.mapbox_logo_padding_bottom))

        when(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> map.zoomTo(mapEvents.initialState)
        }

        mapEvents.viewState.collectOn(mapScope) { state ->
            Kimchi.trackEvent("map_markers", listOf(intProperty("quantity", state.markers.size)))
            mapViewState.emit(state)
            map.showMarkers(state.markers)

            if (state.trackPosition) {
                map.enablePositionTracking()
            } else {
                map.disablePositionTracking()
            }
        }
    }

    override fun onLogMapItemClick(log: LogItemViewState) {
        startStationActivity(log.source)
    }

    override fun onLogListItemClick(item: LogItemViewState) {
        startLogInspectActivity(item.id)
    }

    override fun onLocationEnableClick() {
        Kimchi.trackEvent("location_track_enable")
        foregroundScope.launch {
            permissionGate.withPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
                mapEvents.trackingEnabled.value = true
            }
        }
    }

    override fun onLocationDisableClick() {
        Kimchi.trackEvent("location_track_disable")
        mapEvents.trackingEnabled.value = false
    }

    override fun onSettingsClick() {
        Kimchi.trackNavigation("settings")
        startActivity(SettingsActivity::class)
    }

    override fun onConnectClick() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                permissionGate.withPermissions(*captureEvents.getDriverConnectPermissions().toTypedArray()) {
                    lifecycleScope.launch {
                        if (captureEvents.driverSelection.first() == DriverSelection.Tnc) {
                            startConnectTncActivity(backgroundCaptureServiceIntent)
                        } else {
                            startService(backgroundCaptureServiceIntent)
                        }
                    }
                }
            }
        }
    }

    override fun onDisconnectClick() {
        lifecycleScope.launch {
            captureEvents.disconnectDriver()
            stopService(backgroundCaptureServiceIntent)
        }
    }

    override fun onEnableLocationTransmitClick() {
        captureEvents.locationTransmitState.value = true
    }

    override fun onDisableLocationTransmitClick() {
        captureEvents.locationTransmitState.value = false
    }

    override fun onDriverSelected(selection: DriverSelection) {
        lifecycleScope.launch {
            Kimchi.trackEvent("driver_select", listOf(stringProperty("selection", selection.name)))
            captureEvents.changeDriver(selection)
        }
    }
}
