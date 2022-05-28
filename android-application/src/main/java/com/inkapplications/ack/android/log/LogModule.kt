package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.details.LogDetailsState
import com.inkapplications.ack.android.log.details.LogDetailsViewModelFactory
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.android.extensions.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface LogModule {
    @Binds
    @IntoSet
    fun settings(settings: LogSettings): SettingsProvider

    @Binds
    fun logFactory(factory: CombinedLogItemViewModelFactory): LogItemViewModelFactory

    @Binds
    fun logDetailsFactory(factory: LogDetailsViewModelFactory): ViewModelFactory<LogDetailData, LogDetailsState.LogDetailsViewModel>
}
