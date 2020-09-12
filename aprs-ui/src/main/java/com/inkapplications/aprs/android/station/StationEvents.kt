package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.locale.LocaleSettings
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeBoolean
import com.inkapplications.aprs.data.AprsAccess
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@Reusable
class StationEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val stationViewModelFactory: StationViewModelFactory,
    private val settings: SettingsReadAccess,
    private val localeSettings: LocaleSettings
)  {
    fun stateEvents(id: Long): Flow<StationViewModel> {
        return aprs.findById(id)
            .combine(settings.observeBoolean(localeSettings.preferMetric)) { packet, metric ->
                stationViewModelFactory.create(packet, metric)
            }
    }
}
