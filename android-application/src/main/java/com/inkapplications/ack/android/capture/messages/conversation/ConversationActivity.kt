package com.inkapplications.ack.android.capture.messages.conversation

import android.app.Activity
import androidx.activity.compose.setContent
import com.inkapplications.ack.android.capture.messages.MessageEvents
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Intent Extra used for the addressee of the conversation.
 */
const val EXTRA_ADDRESS = "aprs.conversation.extra.address"

/**
 * Activity for viewing a single conversation's messages with another station.
 */
@AndroidEntryPoint
class ConversationActivity: ExtendedActivity(), ConversationController {
    @Inject
    lateinit var messageEvents: MessageEvents

    private val callsign get() = intent.getStringExtra(EXTRA_ADDRESS)!!.let(::Callsign)

    override fun onCreate() {
        super.onCreate()
        setContent {
            ConversationScreen(this)
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

/**
 * Start a conversation activity for a particular station.
 *
 * @param callsign The callsign of the station to load messages for.
 */
fun Activity.startConversationActivity(callsign: Callsign) {
    Kimchi.trackNavigation("conversation")
    startActivity(ConversationActivity::class) {
        putExtra(EXTRA_ADDRESS, callsign.canonical)
    }
}
