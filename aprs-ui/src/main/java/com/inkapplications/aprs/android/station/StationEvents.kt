package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.data.AprsAccess
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class StationEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val stationViewModelFactory: StationViewModelFactory,
    private val logger: KimchiLogger
)  {
    fun stateEvents(id: Long): Flow<StationViewModel> {
        return aprs.findById(id).map { stationViewModelFactory.create(it) }
    }
}
