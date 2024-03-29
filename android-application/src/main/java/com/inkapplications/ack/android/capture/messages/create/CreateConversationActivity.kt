package com.inkapplications.ack.android.capture.messages.create

import androidx.activity.compose.setContent
import com.inkapplications.ack.android.capture.messages.conversation.startConversationActivity
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity
import kimchi.Kimchi

/**
 * Screen to prompt the user to start a conversation with a specific station.
 */
class CreateConversationActivity: ExtendedActivity(), CreateConversationController {
    override fun onCreate() {
        super.onCreate()

        Kimchi.trackScreen("create_conversation")
        setContent {
            CreateConversationScreen(this)
        }
    }

    override fun onCreateClick(callsign: String) {
        Kimchi.trackEvent("create_conversation_start")
        finish()
        startConversationActivity(Callsign(callsign))
    }
}
