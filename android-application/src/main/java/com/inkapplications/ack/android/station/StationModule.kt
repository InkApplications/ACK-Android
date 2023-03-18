package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class StationModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: StationSettings): SettingsProvider
}
