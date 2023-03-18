package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.BuildConfig
import com.inkapplications.ack.android.firebase.FirebaseSettings
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module(includes = [StaticSettingsModule::class])
@InstallIn(SingletonComponent::class)
class SettingsModule {
    @Provides
    @Reusable
    fun settingsProvider(
        sharedPreferences: SharedPreferenceSettings,
        firebaseSettings: FirebaseSettings
    ): SettingsReadAccess {
        val readProviders = if (BuildConfig.USE_GOOGLE_SERVICES) {
            arrayOf(sharedPreferences, firebaseSettings)
        } else {
            arrayOf(sharedPreferences)
        }

        return PrioritySettingValues(*readProviders)
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
