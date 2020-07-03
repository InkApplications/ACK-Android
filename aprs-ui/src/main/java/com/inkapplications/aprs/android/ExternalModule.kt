package com.inkapplications.aprs.android

import dagger.Module
import dagger.Provides
import dagger.Reusable
import kimchi.Kimchi
import kimchi.logger.KimchiLogger

@Module
class ExternalModule {
    @Provides
    @Reusable
    fun kimchi(): KimchiLogger = Kimchi
}
