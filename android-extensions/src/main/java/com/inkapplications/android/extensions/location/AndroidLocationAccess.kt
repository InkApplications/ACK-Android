package com.inkapplications.android.extensions.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AndroidLocationAccess(
    private val locationManager: LocationManager,
): LocationAccess {
    override fun observeLocationChanges(minTime: Duration, minDistance: Length): Flow<LocationUpdate> = callbackFlow {
        val callback = object: LocationListener {
            override fun onLocationChanged(location: Location) {
                trySendBlocking(LocationUpdate(
                    location = GeoCoordinates(location.latitude.latitude, location.longitude.longitude),
                    altitude = Meters.of(location.altitude),
                ))
            }
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            minTime.inWholeMilliseconds,
            minDistance.value(Meters).toFloat(),
            callback,
        )

        awaitClose {
            locationManager.removeUpdates(callback)
        }
    }
}
