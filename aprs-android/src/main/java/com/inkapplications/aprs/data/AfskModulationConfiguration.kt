package com.inkapplications.aprs.data

import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.WholePercentage
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class AfskModulationConfiguration(
    val preamble: Duration = Duration.milliseconds(100),
    val volume: Percentage = WholePercentage.of(50),
)
