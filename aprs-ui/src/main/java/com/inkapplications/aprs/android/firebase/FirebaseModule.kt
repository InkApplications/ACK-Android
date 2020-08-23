package com.inkapplications.aprs.android.firebase

import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.startup.ApplicationInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class FirebaseModule {
    @Binds
    @IntoSet
    abstract fun initializer(firebaseInitializer: FirebaseInitializer): ApplicationInitializer

    @Binds
    @IntoSet
    abstract fun settings(settings: FirebaseSettings): SettingsReadAccess
}