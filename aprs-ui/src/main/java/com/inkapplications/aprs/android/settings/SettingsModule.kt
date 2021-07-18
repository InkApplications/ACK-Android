package com.inkapplications.aprs.android.settings

import com.inkapplications.aprs.android.firebase.FirebaseSettings
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds

@Module(includes = [StaticSettingsModule::class])
class SettingsModule {
    @Provides
    @Reusable
    fun settingsProvider(
        sharedPreferences: SharedPreferenceSettings,
        firebaseSettings: FirebaseSettings
    ): SettingsReadAccess = PrioritySettingValues(
        sharedPreferences,
        firebaseSettings
    )
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

    @Binds
    @IntoSet
    abstract fun exampleProvider(provider: ExampleSettingsProvider): SettingsProvider
}
