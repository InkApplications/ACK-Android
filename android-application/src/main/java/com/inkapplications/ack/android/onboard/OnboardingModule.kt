package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class OnboardingModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: OnboardSettings): SettingsProvider
}
