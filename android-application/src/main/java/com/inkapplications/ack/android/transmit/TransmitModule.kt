package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.data.drivers.DriverSettingsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class TransmitModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: TransmitSettings): SettingsProvider

    @Binds
    abstract fun driverSettings(settings: PreferenceDriversSettingsProvider): DriverSettingsProvider
}
