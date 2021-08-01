package com.inkapplications.aprs.android.startup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.inkapplications.aprs.android.BuildConfig
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun StartupScreen() = AprsScreen {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(AprsTheme.Spacing.gutter)
    ) {
        val visible = remember { MutableTransitionState(false) }
        visible.targetState = true

        AnimatedVisibility(
            visibleState = visible,
            enter = fadeIn(initialAlpha = 0f),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painterResource(R.drawable.wave),
                    contentDescription = null,
                    tint = AprsTheme.Colors.brand,
                    modifier = Modifier.padding(bottom = AprsTheme.Spacing.content)
                )
                Text(stringResource(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString()))
            }
        }
    }
}