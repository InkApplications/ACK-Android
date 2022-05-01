package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.mapEach
import dagger.Reusable
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    packetStorage: PacketStorage,
    stateFactory: LogItemViewModelFactory,
    settings: SettingsReadAccess,
    localeSettings: LocaleSettings,
    logSettings: LogSettings,
) {
    val logScreenState = settings.observeBoolean(localeSettings.preferMetric)
        .combinePair(settings.observeBoolean(logSettings.filterUnknown))
        .flatMapLatest { (metric, filterUnknown) ->
            packetStorage.findRecent(500)
                .map {
                    if (filterUnknown) it.filter { it.parsed.data !is PacketData.Unknown } else it
                }
                .mapEach { stateFactory.create(it.id, it.parsed, metric) }
        }
        .map { if (it.isEmpty()) LogScreenState.Empty else LogScreenState.LogList(it) }
}
