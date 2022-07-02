package com.inkapplications.ack.android.capture

import android.content.Context
import android.view.View
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.insights.InsightsScreen
import com.inkapplications.ack.android.capture.insights.InsightsViewState
import com.inkapplications.ack.android.capture.log.LogIndexScreen
import com.inkapplications.ack.android.log.index.LogIndexController
import com.inkapplications.ack.android.log.index.LogIndexState
import com.inkapplications.ack.android.map.MapScreen
import com.inkapplications.ack.android.map.MapViewModel
import com.inkapplications.ack.android.capture.messages.MessageIndexScreen
import com.inkapplications.ack.android.capture.messages.index.MessagesScreenController
import com.inkapplications.ack.android.capture.messages.index.MessageIndexScreenState
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.StateLabelledIconButton
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.android.extensions.control.whenDisabled
import com.inkapplications.android.extensions.control.whenOff
import com.inkapplications.android.extensions.control.whenOn
import kimchi.Kimchi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CaptureScreen(
    captureScreenState: State<CaptureScreenViewModel>,
    mapState: State<MapViewModel>,
    insightsState: State<InsightsViewState>,
    logIndexState: State<LogIndexState>,
    logIndexController: LogIndexController,
    messageScreenState: State<MessageIndexScreenState>,
    messagesScreenController: MessagesScreenController,
    mapFactory: (Context) -> View,
    controller: CaptureNavController,
) = AckScreen {
    val navController = rememberNavController()
    val settingsSheetState = rememberBottomSheetScaffoldState()
    SettingsSheetWrapper(
        settingsSheetState = settingsSheetState,
        captureScreenState = captureScreenState,
        captureController = controller,
    ) {
        Column {
            Scaffold(
                bottomBar = { CaptureBottomBar(navController) },
                floatingActionButton = { CaptureSettingsFab(settingsSheetState) },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
            ) {
                CaptureNavHost(
                    navController = navController,
                    mapState = mapState,
                    logIndexState = logIndexState,
                    logIndexController = logIndexController,
                    insightsState = insightsState,
                    messageScreenState = messageScreenState,
                    messagesScreenController = messagesScreenController,
                    mapFactory = mapFactory,
                    captureController = controller,
                )
            }
        }
    }
}

@Composable
private fun CaptureBottomBar(
    navController: NavHostController,
) {
    BottomAppBar(
        backgroundColor = AckTheme.colors.surface,
        contentColor = contentColorFor(AckTheme.colors.surface),
        cutoutShape = RoundedCornerShape(50),
    ) {
        BottomNavigation(
            backgroundColor = AckTheme.colors.surface,
            contentColor = contentColorFor(AckTheme.colors.surface),
        ) {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Map, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_map)) },
                selected = currentRoute == "map",
                onClick = {
                    Kimchi.info("Navigate to map")
                    Kimchi.trackNavigation("map")
                    if (currentRoute != "map") {
                        navController.navigate("map")
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_log)) },
                selected = currentRoute == "log",
                onClick = {
                    Kimchi.info("Navigate to log")
                    Kimchi.trackNavigation("log")
                    if (currentRoute != "log") {
                        navController.navigate("log")
                    }
                }
            )
            Spacer(Modifier.weight(1f, true))
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Mail, contentDescription = null) },
                label = { Text("Messages") },
                selected = currentRoute == "messages",
                onClick = {
                    Kimchi.info("Navigate to messages")
                    Kimchi.trackNavigation("messages")
                    if (currentRoute != "messages") {
                        navController.navigate("messages")
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Lightbulb, contentDescription = null) },
                label = { Text("Insights") },
                selected = currentRoute == "insights",
                onClick = {
                    Kimchi.info("Navigate to insights")
                    Kimchi.trackNavigation("insights")
                    if (currentRoute != "insights") {
                        navController.navigate("insights")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CaptureSettingsFab(
    settingsSheetState: BottomSheetScaffoldState,
) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            scope.launch {
                if (settingsSheetState.bottomSheetState.isExpanded) {
                    settingsSheetState.bottomSheetState.collapse()
                } else {
                    settingsSheetState.bottomSheetState.expand()
                }
            }
        },
        shape = RoundedCornerShape(50),
        backgroundColor = AckTheme.colors.accent,
        contentColor = AckTheme.colors.onAccent
    ) {
        Icon(Icons.Default.SettingsInputAntenna, stringResource(R.string.capture_controls_expand_action))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SettingsSheetWrapper(
    settingsSheetState: BottomSheetScaffoldState,
    captureScreenState: State<CaptureScreenViewModel>,
    captureController: CaptureNavController,
    content: @Composable () -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = { CaptureSettingsSheet(captureScreenState.value, captureController, settingsSheetState) },
        scaffoldState = settingsSheetState,
        sheetPeekHeight = 0.dp,
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun CaptureSettingsSheet(
    captureScreenState: CaptureScreenViewModel,
    captureController: CaptureNavController,
    settingsSheetState: BottomSheetScaffoldState,
) = Column(horizontalAlignment = Alignment.CenterHorizontally) {
    val scope = rememberCoroutineScope()
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(
            onClick = { scope.launch { settingsSheetState.bottomSheetState.collapse() } },
        ) {
            Icon(Icons.Default.DragHandle, stringResource(R.string.capture_controls_collapse_action))
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = AckTheme.dimensions.gutter),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.SettingsInputAntenna,
                contentDescription = null,
                tint = AckTheme.colors.foreground,
                modifier = Modifier.padding(horizontal = AckTheme.dimensions.icon)
            )
            Text(
                text = captureScreenState.callsign ?: "Heck",
                style = AckTheme.typography.h1,
            )
        }
        IconButton(
            onClick = {
                scope.launch {
                    settingsSheetState.bottomSheetState.collapse()
                }
                captureController.onSettingsClick()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.capture_controls_settings_name),
                tint = AckTheme.colors.foreground,
            )
        }
    }
    Row {
        captureScreenState.audioCaptureState.whenOff {
            StateLabelledIconButton(
                state = captureScreenState.audioCaptureState,
                title = stringResource(R.string.capture_controls_audio_capture_disabled_name),
                icon = Icons.Outlined.MicOff,
                onClick = captureController::onAudioCaptureEnableClick,
            )
        }
        captureScreenState.audioCaptureState.whenOn {
            StateLabelledIconButton(
                state = captureScreenState.audioCaptureState,
                title = stringResource(R.string.capture_controls_audio_capture_enabled_name, captureScreenState.audioLevel),
                icon = Icons.Filled.Mic,
                onClick = captureController::onAudioCaptureDisableClick,
            )
        }

        captureScreenState.audioTransmitState.whenDisabled {
            StateLabelledIconButton(
                state = captureScreenState.audioTransmitState,
                icon = Icons.Outlined.VolumeOff,
                title = stringResource(R.string.capture_controls_audio_transmit_disabled_name),
            )
        }
        captureScreenState.audioTransmitState.whenOff {
            StateLabelledIconButton(
                state = captureScreenState.audioTransmitState,
                icon = Icons.Outlined.VolumeOff,
                title = stringResource(R.string.capture_controls_audio_transmit_disabled_name),
                onClick = captureController::onAudioTransmitEnableClick,
            )
        }
        captureScreenState.audioTransmitState.whenOn {
            StateLabelledIconButton(
                state = captureScreenState.audioTransmitState,
                icon = Icons.Filled.VolumeUp,
                title = stringResource(R.string.capture_controls_audio_transmit_enabled_name),
                onClick = captureController::onAudioTransmitDisableClick,
            )
        }

        captureScreenState.internetCaptureState.whenOff {
            StateLabelledIconButton(
                state = captureScreenState.internetCaptureState,
                icon = Icons.Outlined.CloudDownload,
                title = stringResource(R.string.capture_controls_internet_capture_disabled_name),
                onClick = captureController::onInternetCaptureEnableClick,
            )
        }
        captureScreenState.internetCaptureState.whenOn {
            StateLabelledIconButton(
                state = captureScreenState.internetCaptureState,
                icon = Icons.Filled.CloudDownload,
                title = stringResource(R.string.capture_controls_internet_capture_enabled_name),
                onClick = captureController::onInternetCaptureDisableClick,
            )
        }

        captureScreenState.internetTransmitState.whenDisabled {
            StateLabelledIconButton(
                state = captureScreenState.internetTransmitState,
                icon = Icons.Outlined.CloudUpload,
                title = stringResource(R.string.capture_controls_internet_transmit_disabled_name),
            )
        }
        captureScreenState.internetTransmitState.whenOff {
            StateLabelledIconButton(
                state = captureScreenState.internetTransmitState,
                icon = Icons.Outlined.CloudUpload,
                title = stringResource(R.string.capture_controls_internet_transmit_disabled_name),
                onClick = captureController::onInternetTransmitEnableClick,
            )
        }
        captureScreenState.internetTransmitState.whenOn {
            StateLabelledIconButton(
                state = captureScreenState.internetTransmitState,
                icon = Icons.Filled.CloudUpload,
                title = stringResource(R.string.capture_controls_internet_transmit_enabled_name),
                onClick = captureController::onInternetTransmitDisableClick,
            )
        }
    }
}

@Composable
private fun CaptureSettingRow(
    icon: ImageVector,
    iconColor: Color,
    name: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) = Box(modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(vertical = AckTheme.dimensions.clickSafety, horizontal = AckTheme.dimensions.gutter)
            .fillMaxWidth(),
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.padding(end = AckTheme.dimensions.icon))
        Text(name)
    }
}

@Composable
private fun CaptureNavHost(
    navController: NavHostController,
    mapState: State<MapViewModel>,
    logIndexState: State<LogIndexState>,
    logIndexController: LogIndexController,
    insightsState: State<InsightsViewState>,
    messageScreenState: State<MessageIndexScreenState>,
    messagesScreenController: MessagesScreenController,
    mapFactory: (Context) -> View,
    captureController: CaptureNavController,
) {
    NavHost(
        navController = navController,
        startDestination = "map",
        modifier = Modifier.fillMaxSize(),
    ) {
        composable("map") {
            MapScreen(
                state = mapState.value,
                mapFactory = mapFactory,
                onLogItemClick = captureController::onLogMapItemClick,
                onEnableLocation = captureController::onLocationEnableClick,
                onDisableLocation = captureController::onLocationDisableClick,
                bottomContentProtection = AckTheme.dimensions.bottomBarHeight,
            )
        }
        composable("log") {
            LogIndexScreen(
                state = logIndexState,
                controller = logIndexController,
            )
        }
        composable("messages") {
            MessageIndexScreen(
                screenState = messageScreenState,
                controller = messagesScreenController,
                bottomProtection = AckTheme.dimensions.bottomBarHeight,
                bottomContentProtection = AckTheme.dimensions.navigationProtection,
            )
        }
        composable("insights") {
            InsightsScreen(insightsState.value)
        }
    }
}
