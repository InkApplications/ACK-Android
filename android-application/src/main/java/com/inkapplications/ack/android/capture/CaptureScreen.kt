@file:OptIn(ExperimentalLayoutApi::class)

package com.inkapplications.ack.android.capture

import android.content.Context
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.insights.InsightsScreen
import com.inkapplications.ack.android.log.index.LogIndexScreen
import com.inkapplications.ack.android.log.index.LogIndexController
import com.inkapplications.ack.android.map.MapScreen
import com.inkapplications.ack.android.map.MapViewState
import com.inkapplications.ack.android.capture.messages.MessageIndexScreen
import com.inkapplications.ack.android.capture.messages.index.MessagesScreenController
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.connection.readableName
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.AckChip
import com.inkapplications.ack.android.ui.SelectionRow
import com.inkapplications.ack.android.ui.StateLabelledIconButton
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.android.extensions.control.ControlState
import kimchi.Kimchi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CaptureScreen(
    mapState: State<MapViewState>,
    logIndexController: LogIndexController,
    messagesScreenController: MessagesScreenController,
    mapFactory: (Context) -> View,
    controller: CaptureNavController,
    viewModel: CaptureViewModel = hiltViewModel(),
) = AckScreen {
    val settingsSheetState = rememberBottomSheetScaffoldState()
    val navController = rememberNavController()
    navController.enableOnBackPressed(!settingsSheetState.bottomSheetState.isExpanded)
    val captureScreenState = viewModel.controlPanelState.collectAsState()
    SettingsSheetWrapper(
        settingsSheetState = settingsSheetState,
        controlPanelState = captureScreenState,
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
                    logIndexController = logIndexController,
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
        backgroundColor = AckTheme.colors.background,
        contentColor = contentColorFor(AckTheme.colors.surface),
        cutoutShape = RoundedCornerShape(50),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        BottomNavigation(
            backgroundColor = AckTheme.colors.surface,
            contentColor = contentColorFor(AckTheme.colors.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Map, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_map), softWrap = false) },
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
                label = { Text(stringResource(R.string.menu_capture_log), softWrap = false) },
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
                label = { Text("Messages", softWrap = false) },
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
                label = { Text("Insights", softWrap = false) },
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
    controlPanelState: State<ControlPanelState>,
    captureController: CaptureNavController,
    content: @Composable () -> Unit,
) {
    BottomSheetScaffold(
        sheetContent = { CaptureSettingsSheet(controlPanelState.value, captureController, settingsSheetState) },
        scaffoldState = settingsSheetState,
        sheetPeekHeight = 0.dp,
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CaptureSettingsSheet(
    controlPanelState: ControlPanelState,
    captureController: CaptureNavController,
    settingsSheetState: BottomSheetScaffoldState,
) = Column(horizontalAlignment = Alignment.CenterHorizontally) {
    val scope = rememberCoroutineScope()
    BackHandler(enabled = settingsSheetState.bottomSheetState.isExpanded) {
        scope.launch { settingsSheetState.bottomSheetState.collapse() }
    }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AckTheme.spacing.gutter),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (controlPanelState is ControlPanelState.Loaded) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null,
                    tint = AckTheme.colors.foreground,
                    modifier = Modifier.padding(horizontal = AckTheme.spacing.icon)
                )
                Text(
                    text = controlPanelState.userCallsign,
                    style = AckTheme.typography.h1,
                )
            }
        }
        Row {
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
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        var selector by remember { mutableStateOf(false) }

        if (selector) {
            Dialog(
                onDismissRequest = { selector = false },
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        Text(
                            text = "Connection",
                            style = AckTheme.typography.h1,
                            modifier = Modifier.padding(
                                top = AckTheme.spacing.gutter,
                                start = AckTheme.spacing.gutter,
                                end = AckTheme.spacing.gutter,
                                bottom = AckTheme.spacing.content,
                            )
                        )
                        DriverSelection.values().forEach { driver ->
                            SelectionRow(
                                name = stringResource(id = driver.readableName),
                                selected = driver == (controlPanelState as? ControlPanelState.Loaded)?.connectionType,
                                onClick = {
                                    captureController.onDriverSelected(driver)
                                    selector = false
                                },
                            )
                        }
                    }
                }
            }
        }

        AckChip(
            onClick = { selector = true },
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = AckTheme.colors.foreground,
                modifier = Modifier.padding(end = AckTheme.spacing.icon),
            )
            Text(
                text = if (controlPanelState is ControlPanelState.Loaded) {
                    controlPanelState.connection
                } else {
                    stringResource(R.string.capture_controls_connection_type_unknown)
                }
            )
        }
    }
    val volume = (controlPanelState as? ControlPanelState.Loaded)?.volumeLevel
    if (volume != null) {
        LinearProgressIndicator(
            progress = volume,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AckTheme.spacing.item),
        )
    }
    FlowRow(
        horizontalArrangement = Arrangement.Center,
    ) {
        Row {
            StateLabelledIconButton(
                icon = when (controlPanelState.connectState) {
                    ControlState.On -> Icons.Default.WifiTethering
                    ControlState.Off,
                    ControlState.Disabled,
                    ControlState.Hidden -> Icons.Default.WifiTetheringOff
                },
                title = when (controlPanelState.connectState) {
                    ControlState.On -> stringResource(R.string.capture_controls_connection_on)
                    ControlState.Off,
                    ControlState.Disabled,
                    ControlState.Hidden, -> stringResource(R.string.capture_controls_connection_off)
                },
                state = controlPanelState.connectState,
                onClick = {
                    when (it) {
                        ControlState.On -> captureController.onDisconnectClick()
                        ControlState.Off -> captureController.onConnectClick()
                        ControlState.Disabled,
                        ControlState.Hidden -> {}
                    }
                },
            )
            StateLabelledIconButton(
                icon = when (controlPanelState.positionTransmitState) {
                    ControlState.On -> Icons.Default.ModeOfTravel
                    ControlState.Off,
                    ControlState.Disabled,
                    ControlState.Hidden -> Icons.Default.LocationOff
                },
                title = when (controlPanelState.positionTransmitState) {
                    ControlState.On -> stringResource(R.string.capture_controls_position_on)
                    ControlState.Off,
                    ControlState.Disabled,
                    ControlState.Hidden, -> stringResource(R.string.capture_controls_position_off)
                },
                state = controlPanelState.positionTransmitState,
                onClick = {
                    when (it) {
                        ControlState.On -> captureController.onDisableLocationTransmitClick()
                        ControlState.Off -> captureController.onEnableLocationTransmitClick()
                        ControlState.Disabled,
                        ControlState.Hidden -> {}
                    }
                },
            )
        }
    }
}

@Composable
private fun CaptureNavHost(
    navController: NavHostController,
    mapState: State<MapViewState>,
    logIndexController: LogIndexController,
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
                bottomContentProtection = AckTheme.spacing.bottomBarHeight,
            )
        }
        composable("log") {
            LogIndexScreen(
                controller = logIndexController,
            )
        }
        composable("messages") {
            MessageIndexScreen(
                controller = messagesScreenController,
                bottomProtection = AckTheme.spacing.bottomBarHeight,
                bottomContentProtection = AckTheme.spacing.navigationProtection,
            )
        }
        composable("insights") {
            InsightsScreen()
        }
    }
}
