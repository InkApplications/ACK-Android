package com.inkapplications.ack.android.startup

import android.app.Application

interface ApplicationInitializer {
    val priority: Priority get() = Priority.NORMAL

    suspend fun initialize(application: Application)

    enum class Priority {
        HIGH,
        NORMAL,
        LOW,
    }
}

/**
 * Create a static initializer from a lambda.
 */
fun ApplicationInitializer(
    priority: ApplicationInitializer.Priority = ApplicationInitializer.Priority.NORMAL,
    init: Application.() -> Unit
): ApplicationInitializer {
    return object: ApplicationInitializer {
        override suspend fun initialize(application: Application) {
            application.init()
        }
    }
}
