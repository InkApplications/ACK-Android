package com.inkapplications.aprs.android.capture

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.capture.log.AprsLogItem
import com.inkapplications.aprs.android.capture.log.LogItemViewModel
import com.inkapplications.aprs.android.capture.map.MapScreen
import com.inkapplications.aprs.android.capture.map.MapViewModel
import com.inkapplications.aprs.android.trackNavigation
import com.inkapplications.aprs.android.ui.theme.AprsScreen
import com.inkapplications.aprs.android.ui.theme.AprsTheme
import kimchi.Kimchi

@Composable
fun CaptureScreen(
    captureScreenState: State<CaptureScreenViewModel>,
    mapState: State<MapViewModel>,
    logs: State<List<LogItemViewModel>>,
    mapFactory: (Context) -> View,
    controller: CaptureNavController,
) = AprsScreen {
    val navController = rememberNavController()

    Column {
        CaptureAppBar(
            recordingEnabled = captureScreenState.value.recordingEnabled,
            internetServiceVisible = captureScreenState.value.internetServiceVisible,
            internetServiceEnabled = captureScreenState.value.internetServiceEnabled,
            controller = controller,
        )
        Box(
            modifier = Modifier.weight(1f),
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
                        onLogItemClick = controller::onLogItemClick,
                        onEnableLocation = controller::onLocationEnableClick,
                        onDisableLocation = controller::onLocationDisableClick,
                    )
                }
                composable("log") {
                    AprsScreen {
                        LazyColumn {
                            items(logs.value) { log ->
                                AprsLogItem(log, controller::onLogItemClick)
                            }
                        }
                    }
                }
            }
        }

        BottomNavigation(
            backgroundColor = AprsTheme.colors.surface,
            contentColor = contentColorFor(AprsTheme.colors.surface),
        ) {
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Map, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_map)) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == "map",
                onClick = {
                    Kimchi.info("Navigate to map")
                    Kimchi.trackNavigation("map")
                    navController.navigate("map")
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_log)) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == "log",
                onClick = {
                    Kimchi.info("Navigate to log")
                    Kimchi.trackNavigation("log")
                    navController.navigate("log")
                }
            )
        }
    }
}

@Composable
fun CaptureAppBar(
    recordingEnabled: Boolean,
    internetServiceVisible: Boolean,
    internetServiceEnabled: Boolean,
    controller: CaptureNavController,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        backgroundColor = AprsTheme.colors.surface,
        contentColor = contentColorFor(AprsTheme.colors.surface),
        actions = {
            if (recordingEnabled) {
                IconButton(
                    onClick = controller::onRecordingDisableClick,
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = stringResource(R.string.capture_record_disable),
                        tint = AprsTheme.colors.brand,
                    )
                }
            } else {
                IconButton(
                    onClick = controller::onRecordingEnableClick
                ) {
                    Icon(
                        Icons.Default.MicOff,
                        contentDescription = stringResource(R.string.capture_record_enable),
                    )
                }
            }
            when {
                internetServiceVisible && internetServiceEnabled -> {
                    IconButton(
                        onClick = controller::onInternetServiceDisableClick,
                    ) {
                        Icon(
                            Icons.Default.Cloud,
                            contentDescription = "Disable APRS-IS",
                            tint = AprsTheme.colors.brand,
                        )
                    }
                }
                internetServiceVisible && !internetServiceEnabled -> {
                    IconButton(
                        onClick = controller::onInternetServiceEnableClick,
                    ) {
                        Icon(
                            Icons.Default.CloudOff,
                            contentDescription = "Enable APRS-IS"
                        )
                    }
                }
            }
            IconButton(
                onClick = controller::onSettingsClick
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_title)
                )
            }
        }
    )
}
