package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface MapModule {
    @Binds
    @IntoSet
    fun settings(mapSettings: MapSettings): SettingsProvider

    @Binds
    fun markerViewStateFactory(factory: MarkerViewStateFactory): ViewStateFactory<CapturedPacket, MarkerViewState?>
}
