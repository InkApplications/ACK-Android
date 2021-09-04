package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.data.AprsAccess
import dagger.Reusable
import kimchi.Kimchi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class CaptureEvents @Inject constructor(
    private val aprs: AprsAccess
) {
    suspend fun listenForPackets() {
        aprs.incomingAudio.collect {
            Kimchi.debug("APRS Packet Recorded: $it")
        }
    }

    suspend fun listenForInternetPackets() {
        withContext(Dispatchers.IO) {
            aprs.incomingInternet.collect {
                Kimchi.debug("APRS-IS Packet Collected: $it")
            }
        }
    }
}
