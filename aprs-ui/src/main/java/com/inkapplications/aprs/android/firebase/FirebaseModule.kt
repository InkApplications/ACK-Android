package com.inkapplications.aprs.android.firebase

import com.inkapplications.android.extensions.ApplicationInitializer
import com.inkapplications.aprs.android.settings.SettingsReadAccess
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