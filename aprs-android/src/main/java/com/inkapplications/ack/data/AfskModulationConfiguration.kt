package com.inkapplications.ack.data

import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.percent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class AfskModulationConfiguration(
    val preamble: Duration = 100.milliseconds,
    val volume: Percentage = 50.percent,
)
