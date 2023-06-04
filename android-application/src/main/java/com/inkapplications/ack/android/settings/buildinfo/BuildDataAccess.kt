package com.inkapplications.ack.android.settings.buildinfo

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.inkapplications.ack.android.BuildConfig
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Provides access to build and application runtime information.
 */
@Reusable
class BuildDataAccess @Inject constructor(
    googleApiAvailability: GoogleApiAvailability,
    context: Context,
) {
    val buildData: Flow<BuildData> = flowOf(BuildData(
        buildType = BuildConfig.BUILD_TYPE,
        versionName = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        commit = BuildConfig.COMMIT,
        usePlayServices = BuildConfig.USE_GOOGLE_SERVICES,
        playServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS,
    ))
}
