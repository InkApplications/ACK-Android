package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.android.extensions.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface MapModule {
    @Binds
    @IntoSet
    fun settings(mapSettings: MapSettings): SettingsProvider

    @Binds
    fun markerViewModelVactory(factory: MarkerViewModelFactory): ViewModelFactory<CapturedPacket, MarkerViewModel?>
}
