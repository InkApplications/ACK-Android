package com.inkapplications.aprs.android.startup

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds

@Module
abstract class StartupModule {
    @Multibinds
    abstract fun initializers(): @JvmSuppressWildcards Set<ApplicationInitializer>

    @Binds
    abstract fun initializer(composite: CompositeApplicationInitializer): ApplicationInitializer

    @Binds
    @IntoSet
    abstract fun delayedInit(delayedInitializer: DelayedInitializer): ApplicationInitializer
}