package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class StationModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: StationSettings): SettingsProvider
}
