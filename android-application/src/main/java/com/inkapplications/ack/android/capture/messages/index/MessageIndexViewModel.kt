package com.inkapplications.ack.android.capture.messages.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.capture.messages.MessageEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 *  Android viewmodel to load state information for the conversation list.
 */
@HiltViewModel
class MessageIndexViewModel @Inject constructor(
    private val messageEvents: MessageEvents,
    private val messageIndexStateFactory: MessageIndexStateFactory,
): ViewModel() {
    /**
     * List of conversations to be displayed.
     */
    val indexState = messageEvents.latestMessageByConversation
        .map { messageIndexStateFactory.createScreenState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MessageIndexState.Initial)
}
