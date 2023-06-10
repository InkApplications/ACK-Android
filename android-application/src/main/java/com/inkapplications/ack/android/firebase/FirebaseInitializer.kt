package com.inkapplications.ack.android.firebase

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.android.startup.ApplicationInitializer
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Reusable
class FirebaseInitializer @Inject constructor(
    private val settingsProvider: SettingsProvider,
    private val logger: KimchiLogger
): ApplicationInitializer {
    override suspend fun initialize(application: Application) {
        logger.trace("Initializing Firebase.")
        suspendCancellableCoroutine { continuation ->
            settingsProvider.settings
                .map { it.key to it.defaultValue }
                .toMap()
                .onEach { logger.debug("Initializing <${it.key}> with default <${it.value}>") }
                .run(Firebase.remoteConfig::setDefaultsAsync)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }
}
