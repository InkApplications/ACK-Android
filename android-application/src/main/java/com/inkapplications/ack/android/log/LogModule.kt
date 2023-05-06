package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface LogModule {
    @Binds
    @IntoSet
    fun settings(settings: LogSettings): SettingsProvider

    @Binds
    fun logFactory(factory: CombinedLogItemViewStateFactory): LogItemViewStateFactory
}
