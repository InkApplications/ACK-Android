package com.inkapplications.ack.android.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeInt
import com.inkapplications.android.extensions.combineApply
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Android ViewModel to create and store the state of the capture screen.
 */
@HiltViewModel
class CaptureViewModel @Inject constructor(
    captureEvents: CaptureEvents,
    settings: SettingsReadAccess,
    connectionSettings: ConnectionSettings,
    captureScreenStateFactory: CaptureScreenStateFactory,
): ViewModel() {
    val controlPanelState = flowOf(CaptureScreenStateFactory.CaptureData())
        .combineApply(captureEvents.audioListenState) { audioCaptureEnabled = it }
        .combineApply(captureEvents.internetListenState) { internetCaptureEnabled = it }
        .combineApply(captureEvents.audioTransmitState) { audioTransmitEnabled = it }
        .combineApply(captureEvents.internetTransmitState) { internetTransmitEnabled = it }
        .combineApply(settings.observeData(connectionSettings.address)) { currentAddress = it }
        .combineApply(settings.observeInt(connectionSettings.passcode)) { passcode = it }
        .combineApply(captureEvents.audioInputVolume) { inputAudioLevel = it }
        .map { captureScreenStateFactory.controlPanelState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ControlPanelState.Initial)
}
