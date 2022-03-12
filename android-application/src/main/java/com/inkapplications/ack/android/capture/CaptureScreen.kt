package com.inkapplications.ack.android.capture

import android.content.Context
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.alpha
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
import com.inkapplications.ack.android.capture.log.AprsLogItem
import com.inkapplications.ack.android.capture.log.LogItemViewModel
import com.inkapplications.ack.android.capture.map.MapScreen
import com.inkapplications.ack.android.capture.map.MapViewModel
import com.inkapplications.ack.android.capture.messages.MessageScreen
import com.inkapplications.ack.android.capture.messages.MessageScreenController
import com.inkapplications.ack.android.capture.messages.MessageScreenState
import com.inkapplications.ack.android.trackNavigation
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
    logs: State<List<LogItemViewModel>>,
    messageScreenState: State<MessageScreenState>,
    messageScreenController: MessageScreenController,
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
                    logs = logs,
                    messageScreenState = messageScreenState,
                    messageScreenController = messageScreenController,
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
            Spacer(Modifier.weight(1f, true))
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
        backgroundColor = AckTheme.colors.brand,
        contentColor = AckTheme.colors.onBrand
    ) {
        Icon(Icons.Default.SettingsInputAntenna,"")
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CaptureSettingsSheet(
    captureScreenState: CaptureScreenViewModel,
    captureController: CaptureNavController,
    settingsSheetState: BottomSheetScaffoldState,
) = Column {
    val scope = rememberCoroutineScope()
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(AckTheme.spacing.content).fillMaxWidth()
    ) {
        IconButton(
            onClick = { scope.launch { settingsSheetState.bottomSheetState.collapse() } },
        ) {
            Icon(Icons.Default.ExpandMore, "Close Sheet", modifier = Modifier.padding(AckTheme.spacing.clickSafety))
        }
    }
    captureScreenState.audioCaptureState.whenOff {
        CaptureSettingRow(
            icon = Icons.Outlined.MicOff,
            iconColor = AckTheme.colors.foregroundInactive,
            name = "Enable Audio Capture",
            onClick = captureController::onAudioCaptureEnableClick
        )
    }
    captureScreenState.audioCaptureState.whenOn {
        CaptureSettingRow(
            icon = Icons.Filled.Mic,
            iconColor = AckTheme.colors.brand,
            name = "Disable Audio Capture",
            onClick = captureController::onAudioCaptureDisableClick
        )
    }

    captureScreenState.audioTransmitState.whenDisabled {
        CaptureSettingRow(
            icon = Icons.Outlined.VolumeOff,
            iconColor = AckTheme.colors.foregroundInactive,
            name = "Enable Audio Transmit",
            modifier = Modifier.alpha(.6f),
        )
    }
    captureScreenState.audioTransmitState.whenOff {
        CaptureSettingRow(
            icon = Icons.Outlined.VolumeOff,
            iconColor = AckTheme.colors.foregroundInactive,
            name = "Enable Audio Transmit",
            onClick = captureController::onAudioTransmitEnableClick
        )
    }
    captureScreenState.audioTransmitState.whenOn {
        CaptureSettingRow(
            icon = Icons.Filled.VolumeUp,
            iconColor = AckTheme.colors.brand,
            name = "Disable Audio Transmit",
            onClick = captureController::onAudioTransmitDisableClick
        )
    }

    captureScreenState.internetCaptureState.whenOff {
        CaptureSettingRow(
            icon = Icons.Outlined.CloudDownload,
            iconColor = AckTheme.colors.foregroundInactive,
            name = "Enable Internet Capture",
            onClick = captureController::onInternetCaptureEnableClick
        )
    }
    captureScreenState.internetCaptureState.whenOn {
        CaptureSettingRow(
            icon = Icons.Filled.CloudDownload,
            iconColor = AckTheme.colors.brand,
            name = "Disable Internet Capture",
            onClick = captureController::onInternetCaptureDisableClick
        )
    }

    captureScreenState.internetTransmitState.whenDisabled {
        CaptureSettingRow(
            icon = Icons.Outlined.CloudUpload,
            iconColor = AckTheme.colors.foregroundInactive,
            name = "Enable Internet Transmit",
            modifier = Modifier.alpha(.6f),
        )
    }
    captureScreenState.internetTransmitState.whenOff {
        CaptureSettingRow(
            icon = Icons.Outlined.CloudUpload,
            iconColor = AckTheme.colors.foregroundInactive,
            name = "Enable Internet Transmit",
            onClick = captureController::onInternetTransmitEnableClick
        )
    }
    captureScreenState.internetTransmitState.whenOn {
        CaptureSettingRow(
            icon = Icons.Filled.CloudUpload,
            iconColor = AckTheme.colors.brand,
            name = "Disable Internet Transmit",
            onClick = captureController::onInternetTransmitDisableClick
        )
    }

    CaptureSettingRow(
        icon = Icons.Default.Settings,
        iconColor = AckTheme.colors.foreground,
        name = "Settings",
        onClick = {
            scope.launch {
                settingsSheetState.bottomSheetState.collapse()
            }
            captureController.onSettingsClick()
        }
    )
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
            .padding(vertical = AckTheme.spacing.clickSafety, horizontal = AckTheme.spacing.gutter)
            .fillMaxWidth(),
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.padding(end = AckTheme.spacing.icon))
        Text(name)
    }
}

@Composable
private fun CaptureNavHost(
    navController: NavHostController,
    mapState: State<MapViewModel>,
    logs: State<List<LogItemViewModel>>,
    messageScreenState: State<MessageScreenState>,
    messageScreenController: MessageScreenController,
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
                onLogItemClick = captureController::onLogItemClick,
                onEnableLocation = captureController::onLocationEnableClick,
                onDisableLocation = captureController::onLocationDisableClick,
                bottomContentProtection = 60.dp,
            )
        }
        composable("log") {
            LogScreen(
                logs = logs,
                controller = captureController,
            )
        }
        composable("messages") {
            MessageScreen(
                screenState = messageScreenState,
                controller = messageScreenController,
            )
        }
    }
}

@Composable
private fun LogScreen(
    logs: State<List<LogItemViewModel>>,
    controller: CaptureNavController,
) = AckScreen {
    LazyColumn(contentPadding = PaddingValues(bottom = AckTheme.spacing.navigationProtection)) {
        items(logs.value) { log ->
            AprsLogItem(log, controller::onLogItemClick)
        }
    }
}
