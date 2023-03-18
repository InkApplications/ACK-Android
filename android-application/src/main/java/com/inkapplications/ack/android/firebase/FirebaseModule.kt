package com.inkapplications.ack.android.firebase

import com.inkapplications.ack.android.BuildConfig
import com.inkapplications.ack.android.startup.ApplicationInitializer
import com.inkapplications.ack.android.startup.NoOpInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Provides
    @IntoSet
    fun initializer(firebaseInitializer: FirebaseInitializer): ApplicationInitializer {
        if (BuildConfig.USE_GOOGLE_SERVICES) {
            return firebaseInitializer
        }
        return NoOpInitializer
    }
}
