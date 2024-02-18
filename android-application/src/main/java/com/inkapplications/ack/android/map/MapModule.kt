package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.maps.MarkerViewState
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface MapModule {
    @Binds
    @IntoSet
    fun settings(mapSettings: MapSettings): SettingsProvider

    @Binds
    fun markerViewStateFactory(factory: MarkerViewStateFactory): ViewStateFactory<CapturedPacket, MarkerViewState?>
}
