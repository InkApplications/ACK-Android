package com.inkapplications.ack.android.capture.messages.create

import androidx.activity.compose.setContent
import com.inkapplications.ack.android.capture.messages.conversation.startConversationActivity
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ExtendedActivity

class CreateConversationActivity: ExtendedActivity(), CreateConversationController {
    override fun onCreate() {
        super.onCreate()

        setContent {
            CreateConversationScreen(this)
        }
    }

    override fun onCreateClick(callsign: String) {
        startConversationActivity(Callsign(callsign))
    }
}
