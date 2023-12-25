package com.inkapplications.ack.android.startup

import android.app.Application
import kotlinx.coroutines.*

/**
 * Contains and executes the Initialization Job for the application.
 */
class InitJob private constructor(
    private val initializer: ApplicationInitializer,
    private val job: CompletableJob,
): Job by job {
    constructor(initializer: ApplicationInitializer): this(initializer, Job())

    private val scope = CoroutineScope(this + Dispatchers.Main)

    fun init(application: Application) {
        if (job.isCompleted) return
        scope.launch {
            initializer.initialize(application)
            job.complete()
        }
    }
}
