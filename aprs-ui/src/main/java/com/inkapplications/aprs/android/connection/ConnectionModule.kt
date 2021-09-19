package com.inkapplications.aprs.android.connection

import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class ConnectionModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: ConnectionSettings): SettingsProvider
}
