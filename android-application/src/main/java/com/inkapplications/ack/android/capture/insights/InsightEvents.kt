package com.inkapplications.ack.android.capture.insights

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.format.DateTimeFormatter
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class InsightEvents @Inject constructor(
    private val packetStorage: PacketStorage,
    private val settings: SettingsReadAccess,
    private val dateTimeFormatter: DateTimeFormatter,
    private val localeSettings: LocaleSettings,
    private val logger: KimchiLogger,
) {
    val viewState: Flow<InsightsViewState> =
        packetStorage.count()
            .onEach { logger.debug("Building Insights on $it packets") }
            .flatMapLatest { packetCount ->
                if (packetCount == 0) flowOf(InsightsViewState.Empty)
                else packetStorage.findMostRecentByType(PacketData.Weather::class)
                    .combine(settings.observeBoolean(localeSettings.preferMetric)) { weatherPacket, metric ->
                        val temperature = (weatherPacket?.parsed?.data as? PacketData.Weather)?.temperature
                        InsightsViewState.InsightsViewModel(
                            weatherVisible = temperature != null,
                            temperature = temperature?.let { it.format(metric) }.orEmpty(),
                            weatherReporter = weatherPacket?.parsed?.route?.source?.toString().orEmpty(),
                            weatherReportTime = weatherPacket?.received?.let { dateTimeFormatter.formatTimestamp(it) }.orEmpty(),
                            packets = packetCount,
                            stations = 0,
                        )
                    }
                    .combine(packetStorage.countStations()) { viewModel, count ->
                        viewModel.copy(stations = count)
                    }
            }
}
