package com.inkapplications.ack.android.tnc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.data.drivers.TncDriver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android view model for holding data in the TNC connection screen.
 */
@HiltViewModel
class ConnectTncViewModel @Inject constructor(
    deviceAccess: TncDriver,
    stateFactory: ConnectTncStateFactory,
): ViewModel() {
    val state: StateFlow<ConnectTncState> = deviceAccess.deviceData
        .map { stateFactory.create(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectTncState.Initial)
}
