package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.data.AfskModulationConfiguration
import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.Digipeater
import com.inkapplications.karps.structures.Symbol
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
