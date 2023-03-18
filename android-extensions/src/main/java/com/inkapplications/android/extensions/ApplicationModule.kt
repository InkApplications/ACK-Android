package com.inkapplications.android.extensions

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.LocationManager
import androidx.preference.PreferenceManager
import com.inkapplications.android.extensions.format.AndroidResourceDateTimeFormatter
import com.inkapplications.android.extensions.format.DateTimeFormatter
import com.inkapplications.android.extensions.location.AndroidLocationAccess
import com.inkapplications.android.extensions.location.LocationAccess
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [ StaticApplicationModule::class ])
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    fun context(application: Application): Context = application

    @Provides
    fun sharedPreferences(application: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    fun resources(application: Application): Resources = application.resources

    @Provides
    fun locationManager(application: Application) = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun notificationManager(application: Application) = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class StaticApplicationModule {
    @Binds
    abstract fun stringResources(androidStringResources: AndroidStringResources): StringResources

    @Binds
    abstract fun integerResources(android: AndroidIntegerResources): IntegerResources

    @Binds
    abstract fun locationAccess(locationAccess: AndroidLocationAccess): LocationAccess

    @Binds
    abstract fun timeFormat(formatter: AndroidResourceDateTimeFormatter): DateTimeFormatter
}
