package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: OnboardSettings): SettingsProvider
}
