package com.inkapplications.ack.android.capture.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.log.LogItemViewStateFactory
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.coroutines.filterItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

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
    private val logItemViewStateFactory: LogItemViewStateFactory,
    private val clock: Clock = Clock.System,
    private val logger: KimchiLogger = EmptyLogger,
): ViewModel() {
    /**
     * State of the weather insights section.
     */
    val weatherState: StateFlow<InsightsWeatherState> = packetStorage.findMostRecentByType(PacketData.Weather::class)
        .map { if (it?.received?.let { it > clock.now() - 2.hours } == true) it else null }
        .combine(settings.observeBoolean(localeSettings.preferMetric)) { weatherPacket, metric ->
            weatherStateFactory.createState(weatherPacket, metric)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, InsightsWeatherState.Initial)

    /**
     * State of the packet stats section.
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

    /**
     * State of nearby stations broadcasting contact frequencies.
     */
    val stations: StateFlow<NearbyStationsState> = packetStorage.findByStationComments(limit = 10)
        .filterItems { it.received > clock.now() - 1.hours }
        .combine(settings.observeBoolean(localeSettings.preferMetric)) { stations, metric ->
            logItemViewStateFactory.create(stations, metric)
        }
        .map {
            if (it.isEmpty()) NearbyStationsState.Empty
            else NearbyStationsState.StationList(it)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, NearbyStationsState.Initial)
}
