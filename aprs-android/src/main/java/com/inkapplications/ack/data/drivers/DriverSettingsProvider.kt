package com.inkapplications.ack.data.drivers

import com.inkapplications.ack.data.AfskModulationConfiguration
import com.inkapplications.ack.data.ConnectionConfiguration
import kotlinx.coroutines.flow.Flow

interface DriverSettingsProvider {
    val internetServiceConfiguration: Flow<ConnectionConfiguration>
    val afskConfiguration: Flow<AfskModulationConfiguration>
}
