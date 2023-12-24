package com.inkapplications.ack.android.capture.messages.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.capture.CaptureEvents
import com.inkapplications.ack.android.capture.messages.MessageEvents
import com.inkapplications.ack.structures.station.Callsign
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android Viewmodel to load state information for the conversation with [EXTRA_ADDRESS] as the
 * target addressee.
 */
@HiltViewModel
class ConversationViewModel @Inject constructor(
    savedState: SavedStateHandle,
    messageEvents: MessageEvents,
    captureEvents: CaptureEvents,
    conversationViewStateFactory: ConversationViewStateFactory,
): ViewModel() {
    private val conversationAddress = savedState.get<String>(EXTRA_ADDRESS)?.let(::Callsign)!!

    val conversationState = combine(
        messageEvents.conversationList(conversationAddress),
        captureEvents.driverSelection,
        captureEvents.connectionState
    ) { messages, driverSelection, connectionState ->
        conversationViewStateFactory.createMessageList(conversationAddress, messages, connectionState, driverSelection)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, conversationViewStateFactory.createInitial(conversationAddress))
}


