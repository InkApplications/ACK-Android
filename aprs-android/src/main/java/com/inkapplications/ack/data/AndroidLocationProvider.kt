package com.inkapplications.ack.data

import android.Manifest
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import dagger.Reusable
import inkapplications.spondee.measure.metric.meters
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.scale
import inkapplications.spondee.structure.toFloat
import inkapplications.spondee.structure.value
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@Reusable
class AndroidLocationProvider @Inject constructor(
    private val locationManager: LocationManager,
    private val logger: KimchiLogger,
) {
    private val MIN_TIME = 5.minutes.inWholeMilliseconds
    private val MIN_DISTANCE = 1000.scale(Kilo).meters.toFloat()

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private val androidLocationFlow: Flow<Location> = callbackFlow {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                logger.debug("Location Changed: $location")
                trySendBlocking(location)
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                logger.debug("Location Status Changed: $p0 $p1")
            }

            override fun onProviderEnabled(p0: String) {
                logger.debug("Location Provider Enabled: $p0")
            }

            override fun onProviderDisabled(p0: String) {
                logger.debug("Location Provider Disabled: $p0")
            }
        }

        withContext(Dispatchers.Main) {
            locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                ?.run { send(this) }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                listener,
            )
        }
        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    val location: Flow<GeoCoordinates> = androidLocationFlow
        .distinctUntilChanged { a, b ->
            a.distanceTo(b) < MIN_DISTANCE
        }
        .map { location ->
            GeoCoordinates(location.latitude.latitude, location.longitude.longitude)
        }
}
