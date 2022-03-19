package com.inkapplications.ack.android.capture

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.inkapplications.ack.android.capture.log.LogItemViewModel
import com.inkapplications.ack.android.capture.map.*
import com.inkapplications.ack.android.capture.messages.MessageScreenController
import com.inkapplications.ack.android.capture.messages.MessageScreenState
import com.inkapplications.ack.android.component
import com.inkapplications.ack.android.map.Map
import com.inkapplications.ack.android.map.getMap
import com.inkapplications.ack.android.map.lifecycleObserver
import com.inkapplications.ack.android.settings.SettingsActivity
import com.inkapplications.ack.android.station.startStationActivity
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.android.PermissionGate
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import com.inkapplications.coroutines.collectOn
import com.mapbox.mapboxsdk.maps.MapView
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class CaptureActivity: ExtendedActivity(), CaptureNavController {
    private lateinit var mapEventsFactory: MapEventsFactory
    private var mapView: MapView? = null
    private var map: Map? = null
    private var mapScope: CoroutineScope = MainScope()
    private val mapViewModel = MutableStateFlow(MapViewModel())
    private var audioCaptureJob: Job? = null
    private var internetCaptureJob: Job? = null
    private var audioTransmitJob: Job? = null
    private var internetTransmitJob: Job? = null
    private lateinit var captureEvents: CaptureEvents
    private val permissionGate = PermissionGate(this)

    override fun onCreate() {
        super.onCreate()
        mapEventsFactory = component.mapManager()
        val logData = component.logData()

        setContent {
            val captureState = captureEvents.screenState.collectAsState(CaptureScreenViewModel())
            val mapState = mapViewModel.collectAsState()
            val logState = logData.logViewModels.collectAsState(emptyList())
            val messageScreenState = component.messageEvents().screenState.collectAsState(MessageScreenState.Initial)
            val messageScreenController = object: MessageScreenController {
                override fun onCreateMessageClick() {
                }
            }

            CaptureScreen(
                captureScreenState = captureState,
                mapState = mapState,
                logs = logState,
                messageScreenState = messageScreenState,
                messageScreenController = messageScreenController,
                mapFactory = ::createMapView,
                controller = this,
            )
        }
        captureEvents = component.captureEvents()
    }

    private fun createMapView(context: Context): View {
        return if(mapView != null) mapView!! else  MapView(context).also { mapView ->
            this.mapView = mapView

            mapView.getMap(this, ::onMapLoaded)
            lifecycle.addObserver(mapView.lifecycleObserver)

            return mapView
        }
    }

    private fun onMapLoaded(map: Map) {
        this.map = map
        mapScope.cancel()
        mapScope = MainScope()
        val manager = mapEventsFactory.createEventsAccess(map)

        map.initDefaults()

        when(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> map.zoomTo(manager.initialState)
        }

        manager.viewState.collectOn(mapScope) { state ->
            Kimchi.trackEvent("map_markers", listOf(intProperty("quantity", state.markers.size)))
            mapViewModel.emit(state)
            map.showMarkers(state.markers)
        }
    }


    override fun onLogItemClick(log: LogItemViewModel) {
        startStationActivity(log.id)
    }

    override fun onLocationEnableClick() {
        Kimchi.trackEvent("location_track_enable")
        foregroundScope.launch {
            permissionGate.withPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
                map?.enablePositionTracking()
            }
        }
    }

    override fun onLocationDisableClick() {
        Kimchi.trackEvent("location_track_disable")
        map?.disablePositionTracking()
    }

    override fun onAudioCaptureEnableClick() {
        Kimchi.trackEvent("audio_capture_enable")
        foregroundScope.launch {
            permissionGate.withPermissions(*captureEvents.audioCapturePermissions.toTypedArray()) {
                Kimchi.info("Start Recording")
                audioCaptureJob = foregroundScope.launch { captureEvents.listenForPackets() }
            }
        }
    }

    override fun onAudioCaptureDisableClick() {
        Kimchi.trackEvent("audio_capture_disable")
        audioCaptureJob?.cancel()
        audioCaptureJob = null
    }

    override fun onAudioTransmitEnableClick() {
        Kimchi.trackEvent("audio_transmit_enable")
        foregroundScope.launch {
            permissionGate.withPermissions(*captureEvents.audioTransmitPermissions.toTypedArray()) {
                audioTransmitJob?.cancel()
                audioTransmitJob = foregroundScope.launch {
                    captureEvents.transmitAudio()
                }
            }
        }
    }

    override fun onAudioTransmitDisableClick() {
        Kimchi.trackEvent("audio_transmit_disable")
        audioTransmitJob?.cancel()
        audioTransmitJob = null
    }

    override fun onInternetCaptureEnableClick() {
        Kimchi.trackEvent("internet_capture_enable")
        foregroundScope.launch {
            permissionGate.withPermissions(*captureEvents.internetCapturePermissions.toTypedArray()) {
                Kimchi.info("Enable Internet Service")
                internetCaptureJob = foregroundScope.launch { captureEvents.listenForInternetPackets() }
            }
        }
    }

    override fun onInternetCaptureDisableClick() {
        Kimchi.trackEvent("internet_capture_disable")
        internetCaptureJob?.cancel()
        internetCaptureJob = null
    }

    override fun onInternetTransmitEnableClick() {
        Kimchi.trackEvent("internet_transmit_enable")
        foregroundScope.launch {
            permissionGate.withPermissions(*captureEvents.internetTransmitPermissions.toTypedArray()) {
                Kimchi.info("Start Internet Transmit")
                internetTransmitJob?.cancel()
                internetTransmitJob = foregroundScope.launch { captureEvents.transmitInternet() }
            }
        }
    }

    override fun onInternetTransmitDisableClick() {
        Kimchi.trackEvent("internet_transmit_disable")
        internetTransmitJob?.cancel()
        internetTransmitJob = null
    }

    override fun onSettingsClick() {
        Kimchi.trackNavigation("settings")
        startActivity(SettingsActivity::class)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onLowMemory() {
        mapView?.onLowMemory()
        super.onLowMemory()
    }
}
