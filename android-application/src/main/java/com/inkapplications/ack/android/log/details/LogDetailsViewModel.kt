package com.inkapplications.ack.android.log.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.log.LogEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android viewmodel to load state for the log-details screen.
 */
@HiltViewModel
class LogDetailsViewModel @Inject constructor(
    logEvents: LogEvents,
    savedState: SavedStateHandle,
    logDetailsViewStateFactory: LogDetailsViewStateFactory,
): ViewModel() {
    private val logId = savedState.get<Long>(EXTRA_LOG_ID)!!

    /**
     * Detail state about the packet data.
     */
    val detailsState = logEvents.stateEvents(logId)
        .map { logDetailsViewStateFactory.create(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LogDetailsState.Initial)
}
