package com.inkapplications.aprs.android.locale

import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class LocaleModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: LocaleSettings): SettingsProvider
}
