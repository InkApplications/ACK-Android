package com.inkapplications.android.extensions.location

import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Miles
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
interface LocationAccess {
    val lastKnownLocation: LocationUpdate?

    fun observeLocationChanges(
        minTime: Duration = Duration.minutes(5),
        minDistance: Length = Miles.of(5),
    ): Flow<LocationUpdate>
}
