package com.inkapplications.ack.android.capture.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import dagger.hilt.android.lifecycle.HiltViewModel
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Android Viewmodel to load various view state information for the Insights page.
 */
@HiltViewModel
class InsightsViewModel @Inject constructor(
    packetStorage: PacketStorage,
    settings: SettingsReadAccess,
    localeSettings: LocaleSettings,
    private val weatherStateFactory: WeatherStateFactory,
    private val statsStateFactory: StatsStateFactory,
    private val logger: KimchiLogger = EmptyLogger,
): ViewModel() {
    /**
     * State of the weather insights section.
     */
    val weatherState: StateFlow<InsightsWeatherState> = packetStorage.findMostRecentByType(PacketData.Weather::class)
        .combine(settings.observeBoolean(localeSettings.preferMetric)) { weatherPacket, metric ->
            weatherStateFactory.createState(weatherPacket, metric)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, InsightsWeatherState.Initial)

    /**
     * STate of the packet stats section.
     */
    val statsState: StateFlow<InsightsStatsState> = packetStorage.count()
        .onEach { logger.debug("Building stats for $it packets") }
        .combine(packetStorage.countStations()) { packetCount, stationCount ->
            statsStateFactory.createState(
                packetCount = packetCount,
                stationCount = stationCount,
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, InsightsStatsState.Initial)
}
