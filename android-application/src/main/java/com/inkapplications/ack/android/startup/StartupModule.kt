package com.inkapplications.ack.android.startup

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class StartupModule {
    @Multibinds
    abstract fun initializers(): @JvmSuppressWildcards Set<ApplicationInitializer>

    @Binds
    abstract fun initializer(composite: CompositeApplicationInitializer): ApplicationInitializer

    @Binds
    @IntoSet
    abstract fun delayedInit(delayedInitializer: DelayedInitializer): ApplicationInitializer
}
