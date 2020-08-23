package com.inkapplications.aprs.android.startup

import android.app.Application
import dagger.Reusable
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Initializer that delays a minimum amount of time.
 *
 * This is so that the splash screen doesn't flash for too short of a time and
 * look like jank. Since the initializers are run asynchronously, this will not
 * increase the load time, but set a minimum load time.
 */
@Reusable
class DelayedInitializer @Inject constructor(): ApplicationInitializer {
    override suspend fun initialize(application: Application) {
        delay(500)
    }
}