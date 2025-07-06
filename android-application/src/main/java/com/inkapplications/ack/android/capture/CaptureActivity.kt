package com.inkapplications.ack.android.capture

import android.Manifest
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.inkapplications.ack.android.capture.insights.InsightsController
import com.inkapplications.ack.android.capture.messages.conversation.startConversationActivity
import com.inkapplications.ack.android.capture.messages.create.CreateConversationActivity
import com.inkapplications.ack.android.capture.messages.index.MessagesScreenController
import com.inkapplications.ack.android.capture.service.BackgroundCaptureService
import com.inkapplications.ack.android.capture.service.BackgroundCaptureServiceAudio
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.log.details.startLogInspectActivity
import com.inkapplications.ack.android.log.index.LogIndexController
import com.inkapplications.ack.android.map.MapEvents
import com.inkapplications.ack.android.map.MapViewState
import com.inkapplications.ack.android.maps.CameraPositionDefaults
import com.inkapplications.ack.android.maps.MapViewModel
import com.inkapplications.ack.android.settings.SettingsActivity
import com.inkapplications.ack.android.station.startStationActivity
import com.inkapplications.ack.android.tnc.startConnectTncActivity
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.PermissionGate
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.stringProperty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Capture controls and data exploration.
 *
 * This is the primary activity in the application and provides controls
 * to capture APRS packets and explore the data that has been captured.
 */
@AndroidEntryPoint
class CaptureActivity: ExtendedActivity(), CaptureNavController, LogIndexController, InsightsController {
    @Inject
    lateinit var mapEvents: MapEvents

    @Inject
    lateinit var captureEvents: CaptureEvents

    private val permissionGate = PermissionGate(this)
    private val backgroundCaptureServiceIntent by lazy { Intent(this, BackgroundCaptureService::class.java) }
    private val backgroundCaptureAudioServiceIntent by lazy { Intent(this, BackgroundCaptureServiceAudio::class.java) }

    override fun onCreate() {
        super.onCreate()

        setContent {
            val mapState = mapEvents.viewState.collectAsState(MapViewState(
                mapViewModel = MapViewModel(
                    cameraPosition = CameraPositionDefaults.unknownLocation,
                    markers = emptyList(),
                )
            ))
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
                insightsController = this,
                controller = this,
            )
        }
    }

    override fun onLogMapItemClick(log: LogItemViewState) {
        Kimchi.trackEvent("map_log_click")
        startStationActivity(log.source)
    }

    override fun onMapItemClick(captureId: CaptureId?) {
        Kimchi.trackEvent("map_item_select")
        mapEvents.selectedItemId.value = captureId
    }

    override fun onLogListItemClick(item: LogItemViewState) {
        Kimchi.trackEvent("log_item_click")
        startLogInspectActivity(item.id)
    }

    override fun onStationItemClicked(item: LogItemViewState) {
        Kimchi.trackEvent("insights_item_click")
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
        Kimchi.trackEvent("capture_connect")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                permissionGate.withPermissions(*captureEvents.getDriverConnectPermissions().toTypedArray()) {
                    lifecycleScope.launch {
                        when (captureEvents.driverSelection.first()) {
                            DriverSelection.Tnc -> {
                                startConnectTncActivity(backgroundCaptureServiceIntent)
                            }
                            DriverSelection.Audio -> {
                                startForegroundService(backgroundCaptureAudioServiceIntent)
                            }
                            else -> {
                                startForegroundService(backgroundCaptureServiceIntent)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDisconnectClick() {
        Kimchi.trackEvent("capture_disconnect")
        lifecycleScope.launch {
            captureEvents.disconnectDriver()
            stopService(backgroundCaptureServiceIntent)
        }
    }

    override fun onEnableLocationTransmitClick() {
        Kimchi.trackEvent("transmit_enable")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                permissionGate.withPermissions(*captureEvents.getDriverTransmitPermissions().toTypedArray() + Manifest.permission.ACCESS_FINE_LOCATION) {
                    captureEvents.locationTransmitState.value = true
                }
            }
        }
    }

    override fun onDisableLocationTransmitClick() {
        Kimchi.trackEvent("transmit_disable")
        captureEvents.locationTransmitState.value = false
    }

    override fun onDriverSelected(selection: DriverSelection) {
        Kimchi.trackEvent("driver_select", listOf(stringProperty("selection", selection.name)))
        lifecycleScope.launch {
            stopService(backgroundCaptureServiceIntent)
            captureEvents.changeDriver(selection)
        }
    }
}
