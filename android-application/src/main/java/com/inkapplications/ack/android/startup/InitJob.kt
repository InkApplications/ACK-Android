package com.inkapplications.ack.android.startup

import android.app.Application
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
