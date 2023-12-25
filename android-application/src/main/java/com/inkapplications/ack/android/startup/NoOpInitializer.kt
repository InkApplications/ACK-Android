package com.inkapplications.ack.android.startup

/**
 * An Initializer implementation that does nothing.
 *
 * This can be used for testing or as a stand in for disabled services.
 */
val NoOpInitializer = ApplicationInitializer(ApplicationInitializer.Priority.LOW) {}
