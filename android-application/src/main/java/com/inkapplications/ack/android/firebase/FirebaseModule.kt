package com.inkapplications.ack.android.firebase

import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.startup.ApplicationInitializer
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
