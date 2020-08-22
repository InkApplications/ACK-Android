package com.inkapplications.aprs.android.settings

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.Multibinds

@Module(includes = [StaticSettingsModule::class])
class SettingsModule {
    @Provides
    @Reusable
    fun settingsProvider(preferences: SharedPreferenceSettings): SettingsReadAccess {
        return PrioritySettingValues(preferences)
    }
}

@Module
abstract class StaticSettingsModule {
    @Binds
    @Reusable
    abstract fun settingsStorage(preferences: SharedPreferenceSettings): SettingsWriteAccess

    @Multibinds
    abstract fun settingsProviders(): @JvmSuppressWildcards Set<SettingsProvider>

    @Binds
    @Reusable
    abstract fun settingsProvider(provider: CompositeSettingsProvider): SettingsProvider
}