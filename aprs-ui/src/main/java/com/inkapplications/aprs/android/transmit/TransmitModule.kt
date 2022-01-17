package com.inkapplications.aprs.android.transmit

import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class TransmitModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: TransmitSettings): SettingsProvider
}
