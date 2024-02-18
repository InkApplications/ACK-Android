package com.inkapplications.ack.android.station

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.log.LogItemViewStateFactory
import com.inkapplications.ack.android.maps.*
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android viewmodel to load state for the station screen.
 */
@HiltViewModel
class StationViewModel @Inject constructor(
    savedState: SavedStateHandle,
    stationEvents: StationEvents,
    stationInsightViewStateFactory: StationInsightViewStateFactory,
    logItemViewStateFactory: LogItemViewStateFactory,
    markerViewStateFactory: ViewStateFactory<CapturedPacket, MarkerViewState?>,
): androidx.lifecycle.ViewModel() {
    private val stationCallsign = savedState.get<String>(EXTRA_CALLSIGN)!!.let(::Callsign)
    val stationState = stationEvents.stationData(stationCallsign)
        .map { data ->
            StationViewState.Loaded(
                insight = stationInsightViewStateFactory.create(data),
                packets = data.packets.map { packet ->
                    logItemViewStateFactory.create(packet.id, packet.parsed, data.metric)
                },
                mapState = data.packets.firstOrNull { it.parsed.data is Mapable }
                    .let { firstMapable ->
                        MapViewModel(
                            markers = firstMapable?.let { markerViewStateFactory.create(it) }
                                ?.let { listOf(it) }
                                .orEmpty(),
                            cameraPosition = (firstMapable?.parsed?.data as? Mapable)?.coordinates
                                ?.let { MapCameraPosition(it, ZoomLevels.ROADS) }
                                ?: CameraPositionDefaults.unknownLocation,
                        )
                    }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, StationViewState.Initial)
}
