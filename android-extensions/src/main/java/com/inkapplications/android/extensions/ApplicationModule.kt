package com.inkapplications.android.extensions

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.LocationManager
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [ StaticApplicationModule::class ])
class ApplicationModule(private val application: Application) {
    @Provides
    fun context(): Context = application

    @Provides
    fun sharedPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    fun resources(): Resources = application.resources

    @Provides
    fun locationManager() = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}

@Module
internal abstract class StaticApplicationModule {
    @Binds
    abstract fun stringResources(androidStringResources: AndroidStringResources): StringResources
}
