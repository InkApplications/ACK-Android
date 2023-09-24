package com.inkapplications.android.extensions.location

import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.us.miles
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

interface LocationAccess {
    val lastKnownLocation: LocationUpdate?

    fun observeLocationChanges(
        minTime: Duration = 5.minutes,
        minDistance: Length = 5.miles,
    ): Flow<LocationUpdate>
}
