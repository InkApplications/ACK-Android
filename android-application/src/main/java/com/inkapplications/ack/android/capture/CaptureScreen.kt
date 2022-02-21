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
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.WifiTethering
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
import com.inkapplications.ack.android.capture.log.AprsLogItem
import com.inkapplications.ack.android.capture.log.LogItemViewModel
import com.inkapplications.ack.android.capture.map.MapScreen
import com.inkapplications.ack.android.capture.map.MapViewModel
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AprsScreen
import com.inkapplications.ack.android.ui.theme.AprsTheme
import com.inkapplications.android.extensions.control.whenDisabled
import com.inkapplications.android.extensions.control.whenEnabled
import kimchi.Kimchi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CaptureScreen(
    captureScreenState: State<CaptureScreenViewModel>,
    mapState: State<MapViewModel>,
    logs: State<List<LogItemViewModel>>,
    mapFactory: (Context) -> View,
    controller: CaptureNavController,
) = AprsScreen {
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
        backgroundColor = AprsTheme.colors.surface,
        contentColor = contentColorFor(AprsTheme.colors.surface),
        cutoutShape = RoundedCornerShape(50),
    ) {
        BottomNavigation(
            backgroundColor = AprsTheme.colors.surface,
            contentColor = contentColorFor(AprsTheme.colors.surface),
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
            Spacer(Modifier.weight(1f, true))
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
        backgroundColor = AprsTheme.colors.brand,
        contentColor = AprsTheme.colors.onBrand
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
        modifier = Modifier.padding(AprsTheme.spacing.content).fillMaxWidth()
    ) {
        IconButton(
            onClick = { scope.launch { settingsSheetState.bottomSheetState.collapse() } },
        ) {
            Icon(Icons.Default.ExpandMore, "Close Sheet", modifier = Modifier.padding(AprsTheme.spacing.clickSafety))
        }
    }
    captureScreenState.transmitState.whenDisabled {
        CaptureSettingRow(
            icon = Icons.Outlined.WifiTethering,
            iconColor = AprsTheme.colors.foregroundInactive,
            name = "Enable Transmit",
            onClick = captureController::onTransmitEnableClick
        )
    }
    captureScreenState.transmitState.whenEnabled {
        CaptureSettingRow(
            icon = Icons.Filled.WifiTethering,
            iconColor = AprsTheme.colors.brand,
            name = "Disable Transmit",
            onClick = captureController::onTransmitDisableClick
        )
    }

    captureScreenState.recordingState.whenDisabled {
        CaptureSettingRow(
            icon = Icons.Outlined.Mic,
            iconColor = AprsTheme.colors.foregroundInactive,
            name = "Enable Audio Capture",
            onClick = captureController::onRecordingEnableClick
        )
    }
    captureScreenState.recordingState.whenEnabled {
        CaptureSettingRow(
            icon = Icons.Filled.Mic,
            iconColor = AprsTheme.colors.brand,
            name = "Disable Audio Capture",
            onClick = captureController::onRecordingDisableClick
        )
    }

    captureScreenState.internetServiceState.whenDisabled {
        CaptureSettingRow(
            icon = Icons.Outlined.CloudOff,
            iconColor = AprsTheme.colors.foregroundInactive,
            name = "Enable Internet Capture",
            onClick = captureController::onInternetServiceEnableClick
        )
    }
    captureScreenState.internetServiceState.whenEnabled {
        CaptureSettingRow(
            icon = Icons.Filled.Cloud,
            iconColor = AprsTheme.colors.brand,
            name = "Disable Internet Capture",
            onClick = captureController::onInternetServiceDisableClick
        )
    }

    CaptureSettingRow(
        icon = Icons.Default.Settings,
        iconColor = AprsTheme.colors.foreground,
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
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = AprsTheme.spacing.clickSafety, horizontal = AprsTheme.spacing.gutter)
            .fillMaxWidth(),
    ) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.padding(end = AprsTheme.spacing.icon))
        Text(name)
    }
}

@Composable
private fun CaptureNavHost(
    navController: NavHostController,
    mapState: State<MapViewModel>,
    logs: State<List<LogItemViewModel>>,
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
    }
}

@Composable
private fun LogScreen(
    logs: State<List<LogItemViewModel>>,
    controller: CaptureNavController,
) = AprsScreen {
    LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
        items(logs.value) { log ->
            AprsLogItem(log, controller::onLogItemClick)
        }
    }
}
