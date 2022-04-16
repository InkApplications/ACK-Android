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

    @Provides
    fun notificationManager() = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

@Module
internal abstract class StaticApplicationModule {
    @Binds
    abstract fun stringResources(androidStringResources: AndroidStringResources): StringResources

    @Binds
    abstract fun locationAccess(locationAccess: AndroidLocationAccess): LocationAccess

    @Binds
    abstract fun timeFormat(formatter: AndroidResourceDateTimeFormatter): DateTimeFormatter
}
