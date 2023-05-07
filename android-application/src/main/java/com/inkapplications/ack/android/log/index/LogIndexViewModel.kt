package com.inkapplications.ack.android.log.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.log.LogEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android viewmodel to create and hold the state of the Log Index view data.
 */
@HiltViewModel
class LogIndexViewModel @Inject constructor(
    logEvents: LogEvents,
    stateFactory: LogIndexStateFactory,
): ViewModel() {
    val indexState = logEvents.logIndex
        .map { stateFactory.create(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LogIndexState.Initial)
}

