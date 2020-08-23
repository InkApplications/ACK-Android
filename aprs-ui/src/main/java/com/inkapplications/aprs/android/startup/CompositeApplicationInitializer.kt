package com.inkapplications.aprs.android.startup

import android.app.Application
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@Reusable
class CompositeApplicationInitializer @Inject constructor(
    private val initializers: @JvmSuppressWildcards Set<ApplicationInitializer>,
    private val logger: KimchiLogger
): ApplicationInitializer {
    override suspend fun initialize(application: Application) {
        logger.trace("Initializing Application with ${initializers.size} initializers.")
        coroutineScope {
            val jobs = initializers.mapIndexed { index, initializer ->
                async {
                    initializer.initialize(application)
                    logger.trace("#$index: <${initializer.javaClass.simpleName}> complete.")
                }
            }
            jobs.awaitAll()
        }
    }
}