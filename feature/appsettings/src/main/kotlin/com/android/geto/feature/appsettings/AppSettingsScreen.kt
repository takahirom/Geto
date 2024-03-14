/*
 *
 *   Copyright 2023 Einstein Blanco
 *
 *   Licensed under the GNU General Public License v3.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.gnu.org/licenses/gpl-3.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.android.geto.feature.appsettings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.geto.core.data.repository.ShortcutResult
import com.android.geto.core.designsystem.component.GetoLoadingWheel
import com.android.geto.core.designsystem.icon.GetoIcons
import com.android.geto.core.designsystem.theme.GetoTheme
import com.android.geto.core.domain.AppSettingsResult
import com.android.geto.core.model.AppSettings
import com.android.geto.core.model.SettingsType
import com.android.geto.core.model.TargetShortcutInfoCompat
import com.android.geto.core.ui.AppSettingsPreviewParameterProvider
import com.android.geto.feature.appsettings.dialog.appsettings.AddAppSettingsDialog
import com.android.geto.feature.appsettings.dialog.appsettings.AppSettingsDialogState
import com.android.geto.feature.appsettings.dialog.appsettings.rememberAddAppSettingsDialogState
import com.android.geto.feature.appsettings.dialog.copypermissioncommand.CopyPermissionCommandDialog
import com.android.geto.feature.appsettings.dialog.shortcut.AddShortcutDialog
import com.android.geto.feature.appsettings.dialog.shortcut.ShortcutDialogState
import com.android.geto.feature.appsettings.dialog.shortcut.UpdateShortcutDialog
import com.android.geto.feature.appsettings.dialog.shortcut.rememberAddShortcutDialogState
import com.android.geto.feature.appsettings.dialog.shortcut.rememberUpdateShortcutDialogState

@Composable
internal fun AppSettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: AppSettingsViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit
) {
    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val appSettingsUiState = viewModel.appSettingsUiState.collectAsStateWithLifecycle().value

    var showCopyPermissionCommandDialog by remember {
        mutableStateOf(false)
    }

    val launchAppIntent = viewModel.launchAppIntent.collectAsStateWithLifecycle().value

    val secureSettings = viewModel.secureSettings.collectAsStateWithLifecycle().value

    val applicationIcon = viewModel.icon.collectAsStateWithLifecycle().value

    val shortcut = viewModel.shortcut.collectAsStateWithLifecycle().value

    val applyAppSettingsResult =
        viewModel.applyAppSettingsResult.collectAsStateWithLifecycle().value

    val revertAppSettingsResult =
        viewModel.revertAppSettingsResult.collectAsStateWithLifecycle().value

    val shortcutResult = viewModel.shortcutResult.collectAsStateWithLifecycle().value

    val addSettingsDialogState = rememberAddAppSettingsDialogState()

    val addShortcutDialogState = rememberAddShortcutDialogState()

    val updateShortcutDialogState = rememberUpdateShortcutDialogState()

    val keyDebounce = addSettingsDialogState.keyDebounce.collectAsStateWithLifecycle("").value

    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = true) {
        viewModel.getShortcut(viewModel.packageName)
        viewModel.getApplicationIcon()
    }

    LaunchedEffect(key1 = applyAppSettingsResult) {
        applyAppSettingsResult?.let {
            when (it) {
                AppSettingsResult.AppSettingsDisabled -> snackbarHostState.showSnackbar(message = "AppSettingsDisabled")
                AppSettingsResult.EmptyAppSettingsList -> snackbarHostState.showSnackbar(message = "EmptyAppSettingsList")
                AppSettingsResult.Failure -> snackbarHostState.showSnackbar(message = "Failure")
                AppSettingsResult.SecurityException -> showCopyPermissionCommandDialog = true
                AppSettingsResult.Success -> snackbarHostState.showSnackbar(message = "Success")
            }

            viewModel.clearAppSettingsResult()
        }
    }

    LaunchedEffect(key1 = revertAppSettingsResult) {
        revertAppSettingsResult?.let {
            when (it) {
                AppSettingsResult.AppSettingsDisabled -> snackbarHostState.showSnackbar(message = "AppSettingsDisabled")
                AppSettingsResult.EmptyAppSettingsList -> snackbarHostState.showSnackbar(message = "EmptyAppSettingsList")
                AppSettingsResult.Failure -> snackbarHostState.showSnackbar(message = "Failure")
                AppSettingsResult.SecurityException -> showCopyPermissionCommandDialog = true
                AppSettingsResult.Success -> snackbarHostState.showSnackbar(message = "Success")
            }

            viewModel.clearAppSettingsResult()
        }
    }

    LaunchedEffect(key1 = shortcutResult) {
        shortcutResult?.let {
            when (it) {
                ShortcutResult.IDNotFound -> snackbarHostState.showSnackbar(message = "IDNotFound")
                ShortcutResult.ShortcutDisable -> snackbarHostState.showSnackbar(message = "ShortcutDisable")
                ShortcutResult.ShortcutDisableImmutableShortcuts -> snackbarHostState.showSnackbar(
                    message = "ShortcutDisableImmutableShortcuts"
                )

                ShortcutResult.ShortcutEnable -> snackbarHostState.showSnackbar(message = "ShortcutEnable")
                ShortcutResult.ShortcutUpdateFailed -> snackbarHostState.showSnackbar(message = "ShortcutUpdateFailed")
                ShortcutResult.ShortcutUpdateImmutableShortcuts -> snackbarHostState.showSnackbar(
                    message = "ShortcutUpdateImmutableShortcuts"
                )

                ShortcutResult.ShortcutUpdateSuccess -> snackbarHostState.showSnackbar(message = "ShortcutUpdateSuccess")
                ShortcutResult.SupportedLauncher -> snackbarHostState.showSnackbar(message = "SupportedLauncher")
                ShortcutResult.UnsupportedLauncher -> snackbarHostState.showSnackbar(message = "UnsupportedLauncher")
                ShortcutResult.UserIsLocked -> snackbarHostState.showSnackbar(message = "UserIsLocked")
            }

            viewModel.clearShortcutResult()
        }
    }

    LaunchedEffect(key1 = launchAppIntent) {
        launchAppIntent?.let {
            context.startActivity(it)
            viewModel.clearLaunchAppIntent()
        }
    }

    LaunchedEffect(
        key1 = addSettingsDialogState.selectedRadioOptionIndex, key2 = keyDebounce
    ) {
        val settingsType = SettingsType.entries[addSettingsDialogState.selectedRadioOptionIndex]

        viewModel.getSecureSettings(text = addSettingsDialogState.key, settingsType = settingsType)
    }

    LaunchedEffect(key1 = secureSettings) {
        addSettingsDialogState.updateSecureSettings(secureSettings)
    }

    LaunchedEffect(key1 = applicationIcon) {
        applicationIcon?.let {
            addShortcutDialogState.updateIcon(it)
            updateShortcutDialogState.updateIcon(it)
        }
    }

    AppSettingsScreen(modifier = modifier,
                      snackbarHostState = snackbarHostState,
                      appName = viewModel.appName,
                      packageName = viewModel.packageName,
                      appSettingsUiState = appSettingsUiState,
                      addAppSettingsDialogState = addSettingsDialogState,
                      addShortcutDialogState = addShortcutDialogState,
                      updateShortcutDialogState = updateShortcutDialogState,
                      showCopyPermissionCommandDialog = showCopyPermissionCommandDialog,
                      onNavigationIconClick = onNavigationIconClick,
                      onRevertSettingsIconClick = viewModel::revertSettings,
                      onSettingsIconClick = {
                          addSettingsDialogState.updateShowDialog(true)
                      },
                      onShortcutIconClick = {
                          if (shortcut != null) {
                              updateShortcutDialogState.updateShortLabel(shortcut.shortLabel!!)
                              updateShortcutDialogState.updateLongLabel(shortcut.longLabel!!)
                              updateShortcutDialogState.updateShowDialog(true)
                          } else {
                              addShortcutDialogState.updateShowDialog(true)
                          }
                      },
                      onAppSettingsItemCheckBoxChange = viewModel::appSettingsItemCheckBoxChange,
                      onDeleteAppSettingsItem = viewModel::deleteAppSettingsItem,
                      onLaunchApp = viewModel::launchApp,
                      scrollState = scrollState,
                      onAddSettings = viewModel::addSettings,
                      onAddShortcut = viewModel::requestPinShortcut,
                      onUpdateShortcut = viewModel::updateRequestPinShortcut,
                      onRefreshShortcut = {
                          viewModel.getShortcut(viewModel.packageName)
                      },
                      onCopyPermissionCommand = {
                          viewModel.copyPermissionCommand()
                          showCopyPermissionCommandDialog = false
                      },
                      onDismissRequestCopyPermissionCommand = {
                          showCopyPermissionCommandDialog = false
                      })
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppSettingsScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    appName: String,
    packageName: String,
    appSettingsUiState: AppSettingsUiState,
    addAppSettingsDialogState: AppSettingsDialogState,
    addShortcutDialogState: ShortcutDialogState,
    updateShortcutDialogState: ShortcutDialogState,
    showCopyPermissionCommandDialog: Boolean,
    onNavigationIconClick: () -> Unit,
    onRevertSettingsIconClick: () -> Unit,
    onSettingsIconClick: () -> Unit,
    onShortcutIconClick: () -> Unit,
    onAppSettingsItemCheckBoxChange: (Boolean, AppSettings) -> Unit,
    onDeleteAppSettingsItem: (AppSettings) -> Unit,
    onLaunchApp: () -> Unit,
    scrollState: ScrollState,
    onAddSettings: (appSettings: AppSettings) -> Unit,
    onAddShortcut: (targetShortcutInfoCompat: TargetShortcutInfoCompat) -> Unit,
    onUpdateShortcut: (targetShortcutInfoCompat: TargetShortcutInfoCompat) -> Unit,
    onRefreshShortcut: () -> Unit,
    onCopyPermissionCommand: () -> Unit,
    onDismissRequestCopyPermissionCommand: () -> Unit,
) {
    if (addAppSettingsDialogState.showDialog) {
        AddAppSettingsDialog(
            addAppSettingsDialogState = addAppSettingsDialogState,
            scrollState = scrollState,
            onAddSettings = {
                addAppSettingsDialogState.getAppSettings(packageName = packageName)?.let {
                    onAddSettings(it)
                    addAppSettingsDialogState.resetState()
                }
            },
            contentDescription = "Add App Settings Dialog"
        )
    }

    if (showCopyPermissionCommandDialog) {
        CopyPermissionCommandDialog(
            onDismissRequest = onDismissRequestCopyPermissionCommand,
            onCopySettings = onCopyPermissionCommand,
            contentDescription = "Copy Permission Command Dialog"
        )
    }

    if (addShortcutDialogState.showDialog) {
        AddShortcutDialog(shortcutDialogState = addShortcutDialogState, onRefreshShortcut = {
            onRefreshShortcut()
            addShortcutDialogState.updateShowDialog(
                false
            )
        }, onAddShortcut = {
            addShortcutDialogState.getShortcut(
                packageName = packageName
            )?.let {
                onAddShortcut(
                    it
                )
                addShortcutDialogState.resetState()
            }
        }, contentDescription = "Add Shortcut Dialog")
    }

    if (updateShortcutDialogState.showDialog) {
        UpdateShortcutDialog(shortcutDialogState = updateShortcutDialogState, onRefreshShortcut = {
            onRefreshShortcut()
            updateShortcutDialogState.updateShowDialog(false)
        }, onUpdateShortcut = {
            updateShortcutDialogState.getShortcut(packageName = packageName)?.let {
                onUpdateShortcut(it)
                updateShortcutDialogState.resetState()
            }
        }, contentDescription = "Update Shortcut Dialog"
        )
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = appName, maxLines = 1)
        }, modifier = Modifier.testTag("appsettings:topAppBar"), navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = GetoIcons.Back, contentDescription = "Navigation icon"
                )
            }
        })
    }, bottomBar = {
        BottomAppBar(actions = {
            TooltipBox(
                modifier = Modifier.testTag("appsettings:tooltip:revert"),
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text("Revert the applied settings to their original values")
                    }
                },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onRevertSettingsIconClick) {
                    Icon(
                        imageVector = GetoIcons.Refresh, contentDescription = "Revert icon"
                    )
                }
            }

            TooltipBox(
                modifier = Modifier.testTag("appsettings:tooltip:settings"),
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text("Add a custom settings to this app")
                    }
                },
                state = rememberTooltipState()
            ) {

                IconButton(onClick = onSettingsIconClick) {
                    Icon(
                        GetoIcons.Settings,
                        contentDescription = "Settings icon",
                    )
                }
            }

            TooltipBox(
                modifier = Modifier.testTag("appsettings:tooltip:shortcut"),
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text("Add a shortcut to this app")
                    }
                },
                state = rememberTooltipState()
            ) {
                IconButton(onClick = onShortcutIconClick) {

                    Icon(
                        GetoIcons.Shortcut,
                        contentDescription = "Shortcut icon",
                    )
                }
            }
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = onLaunchApp,
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = GetoIcons.Android, contentDescription = "Launch icon"
                )
            }
        })
    }, snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState, modifier = Modifier.testTag("appsettings:snackbar")
        )
    }) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            when (appSettingsUiState) {
                AppSettingsUiState.Empty -> {
                    EmptyState()
                }

                AppSettingsUiState.Loading -> {
                    LoadingState(modifier = Modifier.align(Alignment.Center))
                }

                is AppSettingsUiState.Success -> {
                    SuccessState(
                        appSettingsList = appSettingsUiState.appSettingsList,
                        contentPadding = innerPadding,
                        onAppSettingsItemCheckBoxChange = onAppSettingsItemCheckBoxChange,
                        onDeleteAppSettingsItem = onDeleteAppSettingsItem
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("appsettings:emptyListPlaceHolderScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = GetoIcons.Empty,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Nothing is here")
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    GetoLoadingWheel(
        modifier = modifier, contentDescription = "GetoOverlayLoadingWheel"
    )
}

@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    appSettingsList: List<AppSettings>,
    contentPadding: PaddingValues,
    onAppSettingsItemCheckBoxChange: (Boolean, AppSettings) -> Unit,
    onDeleteAppSettingsItem: (AppSettings) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("appsettings:lazyColumn"),
        contentPadding = contentPadding
    ) {
        appSettings(
            appSettingsList = appSettingsList,
            onAppSettingsItemCheckBoxChange = onAppSettingsItemCheckBoxChange,
            onDeleteAppSettingsItem = onDeleteAppSettingsItem
        )
    }
}

private fun LazyListScope.appSettings(
    appSettingsList: List<AppSettings>,
    onAppSettingsItemCheckBoxChange: (Boolean, AppSettings) -> Unit,
    onDeleteAppSettingsItem: (AppSettings) -> Unit,
) {
    items(appSettingsList) { appSettings ->
        AppSettingsItem(enabled = appSettings.enabled,
                        label = appSettings.label,
                        settingsTypeLabel = appSettings.settingsType.label,
                        key = appSettings.key,
                        onUserAppSettingsItemCheckBoxChange = { check ->
                            onAppSettingsItemCheckBoxChange(
                                check, appSettings
                            )
                        },
                        onDeleteUserAppSettingsItem = {
                            onDeleteAppSettingsItem(appSettings)
                        })
    }
}

@Preview
@Composable
private fun LoadingStatePreview() {
    GetoTheme {
        LoadingState()
    }
}

@Preview
@Composable
private fun EmptyStatePreview() {
    GetoTheme {
        EmptyState()
    }
}

@Preview
@Composable
private fun SuccessStatePreview(
    @PreviewParameter(AppSettingsPreviewParameterProvider::class) appSettingsLists: List<AppSettings>
) {
    GetoTheme {
        SuccessState(appSettingsList = appSettingsLists,
                     contentPadding = PaddingValues(20.dp),
                     onAppSettingsItemCheckBoxChange = { _, _ -> },
                     onDeleteAppSettingsItem = {})
    }
}