package com.inkapplications.ack.android

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kimchi.Kimchi
import kimchi.logger.KimchiLogger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

@Module
@InstallIn(SingletonComponent::class)
class ExternalModule {
    @Provides
    @Reusable
    fun kimchi(): KimchiLogger = Kimchi

    @Provides
    @Reusable
    fun clock(): Clock = Clock.System

    @Provides
    @Reusable
    fun timezone(): TimeZone = TimeZone.currentSystemDefault()
}
