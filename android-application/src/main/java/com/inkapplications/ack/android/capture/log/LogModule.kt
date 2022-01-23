package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class LogModule {
    @Binds
    @IntoSet
    abstract fun settings(settings: LogSettings): SettingsProvider

    @Binds
    abstract fun logFactory(factory: CombinedLogItemViewModelFactory): LogItemViewModelFactory
}
