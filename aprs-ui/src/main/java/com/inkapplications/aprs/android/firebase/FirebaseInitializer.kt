package com.inkapplications.aprs.android.firebase

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.inkapplications.android.extensions.ApplicationInitializer
import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Reusable
import kimchi.logger.KimchiLogger
import javax.inject.Inject

@Reusable
class FirebaseInitializer @Inject constructor(
    private val settingsProvider: SettingsProvider,
    private val logger: KimchiLogger
): ApplicationInitializer {
    override fun onCreate(application: Application) {
        logger.trace("Initializing Firebase.")
        settingsProvider.settings
            .map { it.key to it.defaultValue }
            .toMap()
            .onEach { logger.debug("Initializing <${it.key}> with default <${it.value}>") }
            .run(Firebase.remoteConfig::setDefaults)
    }
}