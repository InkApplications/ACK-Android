package com.inkapplications.aprs.data

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AndroidAprsModule {
    @Provides
    @Singleton
    fun aprsAccess(): AprsAccess = AndroidAprs(AudioDataProcessor())
}
