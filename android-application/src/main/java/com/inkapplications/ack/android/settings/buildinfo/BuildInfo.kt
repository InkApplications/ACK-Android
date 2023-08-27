package com.inkapplications.ack.android.settings.buildinfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.android.extensions.compose.longClickable

/**
 * A column of information text about the build.
 */
@Composable
fun BuildInfo(
    buildInfo: BuildInfoState,
    modifier: Modifier = Modifier,
    onVersionLongPress: () -> Unit = {},
) = Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
    when (buildInfo) {
        BuildInfoState.Initial -> BuildString(
            text = stringResource(R.string.app_name_short),
            onLongPress = onVersionLongPress,
        )
        is BuildInfoState.Debug -> {
            BuildString(stringResource(R.string.application_version_debug), onVersionLongPress)
        }
        is BuildInfoState.Release -> {
            BuildString(
                text = buildInfo.versionStatment,
                onLongPress = onVersionLongPress,
            )
        }
    }
    if (buildInfo is BuildInfoState.ServiceAvailabilityInfo) {
        when (buildInfo.playServices) {
            BuildInfoState.ServiceState.Unavailable -> Text(stringResource(R.string.application_version_qualifier_play_unavailable))
            BuildInfoState.ServiceState.Unconfigured -> Text(stringResource(R.string.application_version_qualifier_play_unconfigured))
            BuildInfoState.ServiceState.Available -> {}
        }
    }
}

@Composable
private fun BuildString(
    text: String,
    onLongPress: (() -> Unit)? = null,
) {
    Text(
        text = text,
        style = AckTheme.typography.caption,
        modifier = Modifier
            .let { if (onLongPress != null) it.longClickable(onLongPress) else it }
            .padding(AckTheme.spacing.clickSafety)
    )
}
