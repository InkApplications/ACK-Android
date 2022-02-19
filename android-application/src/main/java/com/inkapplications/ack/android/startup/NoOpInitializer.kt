package com.inkapplications.ack.android.startup

import android.app.Application

/**
 * An Initializer implementation that does nothing.
 *
 * This can be used for testing or as a stand in for disabled services.
 */
object NoOpInitializer: ApplicationInitializer {
    override suspend fun initialize(application: Application) = Unit
}
