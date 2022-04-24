package com.inkapplications.android.extensions.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import dagger.Reusable
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.value
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Reusable
class AndroidLocationAccess @Inject constructor(
    private val locationManager: LocationManager,
): LocationAccess {
    override val lastKnownLocation: LocationUpdate?
        get() {
            val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val recent = when {
                System.currentTimeMillis() - (lastGps?.time ?: 0) < 30.minutes.inWholeMilliseconds -> lastGps
                System.currentTimeMillis() - (lastNetwork?.time ?: 0) < 30.minutes.inWholeMilliseconds -> lastNetwork
                else -> lastGps ?: lastNetwork
            }

            return recent?.toUpdate()
        }
    override fun observeLocationChanges(minTime: Duration, minDistance: Length): Flow<LocationUpdate> = callbackFlow {
        val callback = object: LocationListener {
            override fun onLocationChanged(location: Location) {
                trySendBlocking(location.toUpdate())
            }
        }
        val providerPreferences = listOf(
            LocationManager.FUSED_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )
        val provider = providerPreferences.firstOrNull { it in locationManager.getProviders(true) } ?: return@callbackFlow
        locationManager.requestLocationUpdates(
            provider,
            minTime.inWholeMilliseconds,
            minDistance.value(Meters).toFloat(),
            callback,
        )

        awaitClose {
            locationManager.removeUpdates(callback)
        }
    }

    private fun Location.toUpdate() = LocationUpdate(
        location = GeoCoordinates(latitude.latitude, longitude.longitude),
        altitude = Meters.of(altitude),
    )
}
