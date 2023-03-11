package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.details.LogDetailsState
import com.inkapplications.ack.android.log.details.LogDetailsViewStateFactory
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface LogModule {
    @Binds
    @IntoSet
    fun settings(settings: LogSettings): SettingsProvider

    @Binds
    fun logFactory(factory: CombinedLogItemViewStateFactory): LogItemViewStateFactory

    @Binds
    fun logDetailsFactory(factory: LogDetailsViewStateFactory): ViewStateFactory<LogDetailData, LogDetailsState.Loaded>
}
