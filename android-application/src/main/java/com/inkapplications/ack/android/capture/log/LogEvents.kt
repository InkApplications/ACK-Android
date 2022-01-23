package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.data.AprsAccess
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.mapEach
import com.inkapplications.karps.structures.PacketData
import dagger.Reusable
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    aprs: AprsAccess,
    stateFactory: LogItemViewModelFactory,
    settings: SettingsReadAccess,
    localeSettings: LocaleSettings,
    logSettings: LogSettings,
) {
    val logViewModels = settings.observeBoolean(localeSettings.preferMetric)
        .combinePair(settings.observeBoolean(logSettings.filterUnknown))
        .flatMapLatest { (metric, filterUnknown) ->
            aprs.findRecent(500)
                .map {
                    if (filterUnknown) it.filter { it.parsed.data !is PacketData.Unknown } else it
                }
                .mapEach { stateFactory.create(it.id, it.parsed, metric) }
        }
}
