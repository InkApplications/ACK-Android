package com.inkapplications.ack.android.capture.messages.conversation

import android.app.Activity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.ack.android.capture.messages.MessageEvents
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kimchi.Kimchi
import javax.inject.Inject

private const val EXTRA_ADDRESS = "aprs.conversation.extra.address"

@AndroidEntryPoint
class ConversationActivity: ExtendedActivity(), ConversationController {
    @Inject
    lateinit var messageEvents: MessageEvents

    private val callsign get() = intent.getStringExtra(EXTRA_ADDRESS)!!.let(::Callsign)

    override fun onCreate() {
        super.onCreate()
        val initialState = ConverstationViewState.Initial(callsign.canonical)

        setContent {
            val state = messageEvents.conversationViewState(callsign).collectAsState(initialState)
            ConversationScreen(state.value, this)
        }
    }

    override fun onNavigateUpPressed() {
        finish()
    }

    override fun onSendMessage(message: String) {
        Kimchi.debug("Sending Message: $message")
        Kimchi.trackEvent("messages_send")
        foregroundScope.launch {
            messageEvents.transmitMessage(callsign, message)
            Kimchi.trace("Message transmit action complete")
        }
    }
}

fun Activity.startConversationActivity(callsign: Callsign) {
    Kimchi.trackNavigation("conversation")
    startActivity(ConversationActivity::class) {
        putExtra(EXTRA_ADDRESS, callsign.canonical)
    }
}
