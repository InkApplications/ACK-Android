package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.coroutines.mapEach
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    aprs: AprsAccess,
    stateFactory: LogStateFactory
) {
    val logViewModels = aprs.findRecent(500).mapEach {
        stateFactory.create(it.id, it.data)
    }
}
