package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.kotlin.mapEach
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    aprs: AprsAccess,
    viewModelFactory: LogViewModelFactory
) {
    val items = aprs.findRecent(500).mapEach {
        LogItem(it.id, viewModelFactory.create(it.data))
    }
}
