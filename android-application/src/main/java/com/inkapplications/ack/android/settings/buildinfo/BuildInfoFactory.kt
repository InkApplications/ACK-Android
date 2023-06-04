package com.inkapplications.ack.android.settings.buildinfo

import com.inkapplications.ack.android.R
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import kimchi.logger.KimchiLogger
import javax.inject.Inject

/**
 * Create a view state based on current build data.
 */
@Reusable
class BuildInfoFactory @Inject constructor(
    private val stringResources: StringResources,
    private val logger: KimchiLogger,
) {
    fun buildInfo(data: BuildData): BuildInfoState {
        val playServices = when {
            !data.usePlayServices -> BuildInfoState.ServiceState.Unconfigured
            !data.playServicesAvailable -> BuildInfoState.ServiceState.Unavailable
            else -> BuildInfoState.ServiceState.Available
        }
        if (data.buildType == "debug") {
            return BuildInfoState.Debug(playServices)
        }

        return BuildInfoState.Release(
            versionStatment = stringResources.getString(
                key = R.string.application_version_full,
                arguments = arrayOf(
                    data.versionName,
                    data.versionCode.toString(),
                    data.commit?.take(7) ?: stringResources.getString(R.string.application_version_commit_missing).also {
                        logger.error("Release Commit without Commit Hash")
                    },
                )
            ),
            playServices = playServices
        )
    }
}
