package com.inkapplications.ack.android.capture

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.inkapplications.ack.android.capture.log.LogItemViewModel
import com.inkapplications.ack.android.capture.map.*
import com.inkapplications.ack.android.component
import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.Map
import com.inkapplications.ack.android.map.getMap
import com.inkapplications.ack.android.map.lifecycleObserver
import com.inkapplications.ack.android.settings.SettingsActivity
import com.inkapplications.ack.android.station.startStationActivity
import com.inkapplications.ack.android.trackNavigation
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
    private var recording: Job? = null
    private var isConnection: Job? = null
    private var transmitJob: Job? = null
    private lateinit var captureEvents: CaptureEvents
    private val mapLocationPermissionRequest: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Kimchi.trackEvent("location_permission_grant")
            onLocationEnableClick()
        } else {
            Kimchi.trackEvent("location_permission_deny")
        }
    }

    private val internetLocationPermissionRequest: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Kimchi.trackEvent("location_permission_grant")
            onInternetLocationPermissionGranted()
        } else {
            Kimchi.trackEvent("location_permission_deny")
        }
    }

    private val transmitPremissionRequest: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Kimchi.trackEvent("location_permission_grant")
            onTransmitPermissionsGranted()
        } else {
            Kimchi.trackEvent("location_permission_deny")
        }
    }

    private val micPermissionRequest: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Kimchi.trackEvent("record_permission_grant")
            onRecordingPermissionsGranted()
        } else {
            Kimchi.trackEvent("record_permission_deny")
        }
    }

    override fun onCreate() {
        super.onCreate()
        mapEventsFactory = component.mapManager()
        val logData = component.logData()

        setContent {
            val captureState = captureEvents.screenState.collectAsState(CaptureScreenViewModel())
            val mapState = mapViewModel.collectAsState()
            val logState = logData.logViewModels.collectAsState(emptyList())

            CaptureScreen(
                captureScreenState = captureState,
                mapState = mapState,
                logs = logState,
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

        map.setCamera(CameraPositionDefaults.unknownLocation)

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
        when(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> map?.enablePositionTracking()
            else -> mapLocationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onLocationDisableClick() {
        map?.disablePositionTracking()
    }
    override fun onRecordingEnableClick() {
        Kimchi.trackEvent("record_enable")
        when(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PackageManager.PERMISSION_GRANTED -> onRecordingPermissionsGranted()
            else -> micPermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun onRecordingPermissionsGranted() {
        Kimchi.info("Start Recording")
        recording = foregroundScope.launch { captureEvents.listenForPackets() }
    }

    override fun onRecordingDisableClick() {
        Kimchi.trackEvent("record_disable")
        recording?.cancel()
        recording = null
    }

    override fun onInternetServiceEnableClick() {
        Kimchi.trackEvent("internet_enable")
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> onInternetLocationPermissionGranted()
            else -> internetLocationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun onInternetLocationPermissionGranted() {
        Kimchi.info("Enable Internet Service")
        isConnection = foregroundScope.launch { captureEvents.listenForInternetPackets() }
    }

    override fun onInternetServiceDisableClick() {
        Kimchi.trackEvent("internet_disable")
        isConnection?.cancel()
        isConnection = null
    }

    override fun onTransmitEnableClick() {
        Kimchi.trackEvent("transmit_enable")
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PackageManager.PERMISSION_GRANTED -> onTransmitPermissionsGranted()
            else -> transmitPremissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onTransmitDisableClick() {
        Kimchi.trackEvent("transmit_disable")
        transmitJob?.cancel()
        transmitJob = null
    }

    private fun onTransmitPermissionsGranted() {
        transmitJob?.cancel()
        transmitJob = foregroundScope.launch {
            captureEvents.transmitLoop()
        }
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
