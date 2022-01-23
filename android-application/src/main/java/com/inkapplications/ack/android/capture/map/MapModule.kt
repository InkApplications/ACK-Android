package com.inkapplications.ack.android.capture.map

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class MapModule {
    @Binds
    @IntoSet
    abstract fun settings(mapSettings: MapSettings): SettingsProvider
}
