package com.inkapplications.ack.android.capture

import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.station.StationAddress
import inkapplications.spondee.measure.Length
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class TransmitPrototype(
    val path: List<Digipeater>,
    val destination: StationAddress,
    val callsign: StationAddress,
    val symbol: Symbol,
    val comment: String,
    val minRate: Duration,
    val maxRate: Duration,
    val distance: Length,
)
