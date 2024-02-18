package com.inkapplications.ack.android.startup

import android.app.Application
import com.inkapplications.ack.android.capture.service.CaptureServiceNotifications
import com.inkapplications.ack.android.maps.MapsImplementation
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.Multibinds
import javax.inject.Singleton

@Module(includes = [StartupModuleBindings::class])
@InstallIn(SingletonComponent::class)
class StartupModule {
    @Singleton
    @Provides
    fun initJob(initializer: ApplicationInitializer) = InitJob(initializer)

    @Provides
    @ElementsIntoSet
    fun initializers(
        captureService: CaptureServiceNotifications
    ): Set<ApplicationInitializer> = setOf(
        object: ApplicationInitializer {
            override suspend fun initialize(application: Application) {
                MapsImplementation.initialize(application)
            }
        },
        KimchiInitializer,
        captureService,
    )
}

@Module
private abstract class StartupModuleBindings {
    @Multibinds
    abstract fun initializerBinding(): @JvmSuppressWildcards Set<ApplicationInitializer>

    @Binds
    abstract fun initializer(composite: CompositeApplicationInitializer): ApplicationInitializer
}
