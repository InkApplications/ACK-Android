package com.inkapplications.ack.android.startup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.settings.buildinfo.BuildInfo
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoState
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun StartupScreen(
    viewModel: StartupViewModel = hiltViewModel()
) = AckScreen {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(AckTheme.spacing.gutter)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val buildInfoState = viewModel.buildInfoState.collectAsState()
        val visible = remember { MutableTransitionState(false) }

        if (buildInfoState.value !is BuildInfoState.Initial) {
            visible.targetState = true
        }

        AnimatedVisibility(
            visibleState = visible,
            enter = fadeIn(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painterResource(R.drawable.wave),
                    contentDescription = null,
                    tint = AckTheme.colors.accent,
                    modifier = Modifier.padding(bottom = AckTheme.spacing.content)
                )
                BuildInfo(buildInfoState.value)
            }
        }
    }
}
