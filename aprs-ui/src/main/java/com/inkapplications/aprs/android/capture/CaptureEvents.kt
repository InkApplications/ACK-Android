package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.data.AprsAccess
import dagger.Reusable
import kimchi.Kimchi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@Reusable
class CaptureEvents @Inject constructor(
    private val aprs: AprsAccess
) {
    suspend fun listenForPackets() {
        aprs.incoming.collect {
            Kimchi.debug("APRS Packet Recorded: $it")
        }
    }
}
