package com.inkapplications.aprs.data

import dagger.Module
import dagger.Provides
import kimchi.logger.KimchiLogger
import javax.inject.Singleton

@Module
object AndroidAprsModule {
    @Provides
    @Singleton
    fun aprsAccess(
        logger: KimchiLogger
    ): AprsAccess {
        val audioCapture = AudioDataCapture(logger)
        val audioProcessor = AudioDataProcessor(audioCapture)

        return AndroidAprs(audioProcessor, logger)
    }
}
