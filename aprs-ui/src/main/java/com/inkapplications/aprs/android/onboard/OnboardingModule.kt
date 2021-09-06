package com.inkapplications.aprs.android.onboard

import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class OnboardingModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: OnboardSettings): SettingsProvider
}
