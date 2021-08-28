package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.locale.LocaleSettings
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeBoolean
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.mapEach
import com.inkapplications.karps.structures.AprsPacket
import dagger.Reusable
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.log

@Reusable
class LogEvents @Inject constructor(
    aprs: AprsAccess,
    stateFactory: LogStateFactory,
    settings: SettingsReadAccess,
    localeSettings: LocaleSettings,
    logSettings: LogSettings,
) {
    val logViewModels = settings.observeBoolean(localeSettings.preferMetric)
        .combinePair(settings.observeBoolean(logSettings.filterUnknown))
        .flatMapLatest { (metric, filterUnknown) ->
            aprs.findRecent(500)
                .map {
                    if (filterUnknown) it.filter { it.data !is AprsPacket.Unknown } else it
                }
                .mapEach { stateFactory.create(it.id, it.data, metric) }
        }
}
