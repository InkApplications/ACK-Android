package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.data.drivers.DriverSettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class TransmitModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: TransmitSettings): SettingsProvider

    @Binds
    abstract fun driverSettings(settings: PreferenceDriversSettingsProvider): DriverSettingsProvider
}
