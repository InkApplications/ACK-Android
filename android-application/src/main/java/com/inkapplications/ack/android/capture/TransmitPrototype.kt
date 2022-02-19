package com.inkapplications.ack.android.capture

import com.inkapplications.ack.data.AfskModulationConfiguration
import com.inkapplications.ack.structures.Address
import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.Symbol
import inkapplications.spondee.measure.Length
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class TransmitPrototype(
    val path: List<Digipeater>,
    val destination: Address,
    val callsign: Address,
    val symbol: Symbol,
    val comment: String,
    val minRate: Duration,
    val maxRate: Duration,
    val distance: Length,
    val afskConfiguration: AfskModulationConfiguration = AfskModulationConfiguration(),
)
