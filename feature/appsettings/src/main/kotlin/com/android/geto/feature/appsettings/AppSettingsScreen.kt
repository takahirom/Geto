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

import android.graphics.drawable.Drawable
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.geto.core.designsystem.component.GetoLoadingWheel
import com.android.geto.core.designsystem.icon.GetoIcons
import com.android.geto.core.designsystem.theme.GetoTheme
import com.android.geto.core.domain.AppSettingsResult
import com.android.geto.core.domain.AutoLaunchResult
import com.android.geto.core.model.AppSetting
import com.android.geto.core.model.MappedShortcutInfoCompat
import com.android.geto.core.model.SecureSetting
import com.android.geto.core.model.SettingType
import com.android.geto.core.ui.AppSettingsPreviewParameterProvider
import com.android.geto.core.ui.DevicePreviews
import com.android.geto.feature.appsettings.dialog.appsetting.AppSettingDialog
import com.android.geto.feature.appsettings.dialog.appsetting.AppSettingDialogState
import com.android.geto.feature.appsettings.dialog.appsetting.rememberAppSettingDialogState
import com.android.geto.feature.appsettings.dialog.copypermissioncommand.CopyPermissionCommandDialog
import com.android.geto.feature.appsettings.dialog.copypermissioncommand.CopyPermissionCommandDialogState
import com.android.geto.feature.appsettings.dialog.copypermissioncommand.rememberCopyPermissionCommandDialogState
import com.android.geto.feature.appsettings.dialog.shortcut.AddShortcutDialog
import com.android.geto.feature.appsettings.dialog.shortcut.ShortcutDialogState
import com.android.geto.feature.appsettings.dialog.shortcut.UpdateShortcutDialog
import com.android.geto.feature.appsettings.dialog.shortcut.rememberShortcutDialogState

@Composable
internal fun AppSettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: AppSettingsViewModel = hiltViewModel(),
    onNavigationIconClick: () -> Unit,
) {
    val appSettingsUiState = viewModel.appSettingUiState.collectAsStateWithLifecycle().value

    val secureSettings = viewModel.secureSettings.collectAsStateWithLifecycle().value

    val applyAppSettingsResult =
        viewModel.applyAppSettingsResult.collectAsStateWithLifecycle().value

    val revertAppSettingsResult =
        viewModel.revertAppSettingsResult.collectAsStateWithLifecycle().value

    val applicationIcon = viewModel.applicationIcon.collectAsStateWithLifecycle().value

    val mappedShortcutInfoCompat =
        viewModel.mappedShortcutInfoCompat.collectAsStateWithLifecycle().value

    val shortcutResult = viewModel.shortcutResult.collectAsStateWithLifecycle().value

    val clipboardResult = viewModel.clipboardResult.collectAsStateWithLifecycle().value

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    AppSettingsScreen(
        modifier = modifier,
        packageName = viewModel.packageName,
        appName = viewModel.appName,
        appSettingsUiState = appSettingsUiState,
        snackbarHostState = snackbarHostState,
        applicationIcon = applicationIcon,
        mappedShortcutInfoCompat = mappedShortcutInfoCompat,
        secureSettings = secureSettings,
        applyAppSettingsResult = applyAppSettingsResult,
        revertAppSettingsResult = revertAppSettingsResult,
        shortcutResult = shortcutResult,
        clipboardResult = clipboardResult,
        onNavigationIconClick = onNavigationIconClick,
        onRevertAppSettings = viewModel::revertAppSettings,
        onGetShortcut = viewModel::getShortcut,
        onCheckAppSetting = viewModel::checkAppSetting,
        onDeleteAppSetting = viewModel::deleteAppSetting,
        onLaunchApp = viewModel::applyAppSettings,
        onAutoLaunchApp = viewModel::autoLaunchApp,
        onGetApplicationIcon = viewModel::getApplicationIcon,
        onResetAppSettingsResult = viewModel::resetAppSettingsResult,
        onResetShortcutResult = viewModel::resetShortcutResult,
        onResetClipboardResult = viewModel::resetClipboardResult,
        onGetSecureSettingsByName = viewModel::getSecureSettingsByName,
        onAddAppSetting = viewModel::addAppSettings,
        onCopyPermissionCommand = viewModel::copyPermissionCommand,
        onAddShortcut = viewModel::requestPinShortcut,
        onUpdateShortcut = viewModel::updateRequestPinShortcut,
    )
}

@VisibleForTesting
@Composable
internal fun AppSettingsScreen(
    modifier: Modifier = Modifier,
    packageName: String,
    appName: String,
    appSettingsUiState: AppSettingsUiState,
    snackbarHostState: SnackbarHostState,
    applicationIcon: Drawable?,
    mappedShortcutInfoCompat: MappedShortcutInfoCompat?,
    secureSettings: List<SecureSetting>,
    applyAppSettingsResult: AppSettingsResult,
    revertAppSettingsResult: AppSettingsResult,
    shortcutResult: String?,
    clipboardResult: String?,
    onNavigationIconClick: () -> Unit,
    onRevertAppSettings: () -> Unit,
    onGetShortcut: () -> Unit,
    onCheckAppSetting: (Boolean, AppSetting) -> Unit,
    onDeleteAppSetting: (AppSetting) -> Unit,
    onLaunchApp: () -> Unit,
    onAutoLaunchApp: () -> Unit,
    onGetApplicationIcon: () -> Unit,
    onResetAppSettingsResult: () -> Unit,
    onResetShortcutResult: () -> Unit,
    onResetClipboardResult: () -> Unit,
    onGetSecureSettingsByName: (SettingType, String) -> Unit,
    onAddAppSetting: (AppSetting) -> Unit,
    onCopyPermissionCommand: () -> Unit,
    onAddShortcut: (MappedShortcutInfoCompat) -> Unit,
    onUpdateShortcut: (MappedShortcutInfoCompat) -> Unit,
) {
    val copyPermissionCommandDialogState = rememberCopyPermissionCommandDialogState()

    val appSettingDialogState = rememberAppSettingDialogState()

    val addShortcutDialogState = rememberShortcutDialogState()

    val updateShortcutDialogState = rememberShortcutDialogState()

    AppSettingsLaunchedEffects(
        snackbarHostState = snackbarHostState,
        copyPermissionCommandDialogState = copyPermissionCommandDialogState,
        appSettingDialogState = appSettingDialogState,
        addShortcutDialogState = addShortcutDialogState,
        updateShortcutDialogState = updateShortcutDialogState,
        applicationIcon = applicationIcon,
        secureSettings = secureSettings,
        applyAppSettingsResult = applyAppSettingsResult,
        revertAppSettingsResult = revertAppSettingsResult,
        shortcutResult = shortcutResult,
        clipboardResult = clipboardResult,
        onAutoLaunchApp = onAutoLaunchApp,
        onGetApplicationIcon = onGetApplicationIcon,
        onGetShortcut = onGetShortcut,
        onResetAppSettingsResult = onResetAppSettingsResult,
        onResetShortcutResult = onResetShortcutResult,
        onResetClipboardResult = onResetClipboardResult,
        onGetSecureSettingsByName = onGetSecureSettingsByName,
    )

    AppSettingsDialogs(
        copyPermissionCommandDialogState = copyPermissionCommandDialogState,
        appSettingDialogState = appSettingDialogState,
        addShortcutDialogState = addShortcutDialogState,
        updateShortcutDialogState = updateShortcutDialogState,
        packageName = packageName,
        onAddAppSetting = onAddAppSetting,
        onCopyPermissionCommand = onCopyPermissionCommand,
        onAddShortcut = onAddShortcut,
        onUpdateShortcut = onUpdateShortcut,
    )

    Scaffold(
        topBar = {
            AppSettingsTopAppBar(
                title = appName,
                onNavigationIconClick = onNavigationIconClick,
            )
        },
        bottomBar = {
            AppSettingsBottomAppBar(
                mappedShortcutInfoCompat = mappedShortcutInfoCompat,
                onRevertSettingsIconClick = onRevertAppSettings,
                onSettingsIconClick = {
                    appSettingDialogState.updateShowDialog(true)
                },
                onAddShortcutIconClick = {
                    addShortcutDialogState.updateShowDialog(true)
                },
                onUpdateShortcutIconClick = {
                    updateShortcutDialogState.updateShortLabel(it.shortLabel)
                    updateShortcutDialogState.updateLongLabel(it.longLabel)
                    updateShortcutDialogState.updateShowDialog(true)
                },
                onLaunchApp = onLaunchApp,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("appSettings:snackbar"),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding),
        ) {
            when (appSettingsUiState) {
                AppSettingsUiState.Loading -> {
                    LoadingState(modifier = Modifier.align(Alignment.Center))
                }

                is AppSettingsUiState.Success -> {
                    if (appSettingsUiState.appSettingList.isNotEmpty()) {
                        SuccessState(
                            appSettingsUiState = appSettingsUiState,
                            contentPadding = innerPadding,
                            onAppSettingsItemCheckBoxChange = onCheckAppSetting,
                            onDeleteAppSettingsItem = onDeleteAppSetting,
                        )
                    } else {
                        EmptyState(text = stringResource(R.string.add_your_first_settings))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppSettingsLaunchedEffects(
    snackbarHostState: SnackbarHostState,
    copyPermissionCommandDialogState: CopyPermissionCommandDialogState,
    appSettingDialogState: AppSettingDialogState,
    addShortcutDialogState: ShortcutDialogState,
    updateShortcutDialogState: ShortcutDialogState,
    applicationIcon: Drawable?,
    secureSettings: List<SecureSetting>,
    applyAppSettingsResult: AppSettingsResult,
    revertAppSettingsResult: AppSettingsResult,
    shortcutResult: String?,
    clipboardResult: String?,
    onAutoLaunchApp: () -> Unit,
    onGetApplicationIcon: () -> Unit,
    onGetShortcut: () -> Unit,
    onResetAppSettingsResult: () -> Unit,
    onResetShortcutResult: () -> Unit,
    onResetClipboardResult: () -> Unit,
    onGetSecureSettingsByName: (SettingType, String) -> Unit,
) {
    val appSettingsDisabled = stringResource(id = R.string.app_settings_disabled)
    val emptyAppSettingsList = stringResource(id = R.string.empty_app_settings_list)
    val applyFailure = stringResource(id = R.string.apply_failure)
    val revertFailure = stringResource(id = R.string.revert_failure)
    val revertSuccess = stringResource(id = R.string.revert_success)
    val invalidValues = stringResource(R.string.settings_has_invalid_values)

    val context = LocalContext.current

    val keyDebounce = appSettingDialogState.keyDebounce.collectAsStateWithLifecycle("").value

    LaunchedEffect(key1 = true) {
        onAutoLaunchApp()
        onGetApplicationIcon()
        onGetShortcut()
    }

    LaunchedEffect(key1 = applyAppSettingsResult) {
        when (applyAppSettingsResult) {
            AppSettingsResult.DisabledAppSettings -> snackbarHostState.showSnackbar(message = appSettingsDisabled)
            AppSettingsResult.EmptyAppSettings -> snackbarHostState.showSnackbar(message = emptyAppSettingsList)
            AppSettingsResult.Failure -> snackbarHostState.showSnackbar(message = applyFailure)
            AppSettingsResult.SecurityException -> copyPermissionCommandDialogState.updateShowDialog(
                true,
            )

            is AppSettingsResult.Success -> applyAppSettingsResult.launchIntent?.let(context::startActivity)
            AutoLaunchResult.Ignore -> Unit
            AppSettingsResult.IllegalArgumentException -> snackbarHostState.showSnackbar(message = invalidValues)
            AppSettingsResult.NoResult -> Unit
        }

        onResetAppSettingsResult()
    }

    LaunchedEffect(key1 = revertAppSettingsResult) {
        when (revertAppSettingsResult) {
            AppSettingsResult.DisabledAppSettings -> snackbarHostState.showSnackbar(message = appSettingsDisabled)
            AppSettingsResult.EmptyAppSettings -> snackbarHostState.showSnackbar(message = emptyAppSettingsList)
            AppSettingsResult.Failure -> snackbarHostState.showSnackbar(message = revertFailure)
            AppSettingsResult.SecurityException -> copyPermissionCommandDialogState.updateShowDialog(
                true,
            )

            is AppSettingsResult.Success -> snackbarHostState.showSnackbar(message = revertSuccess)
            AutoLaunchResult.Ignore -> Unit
            AppSettingsResult.IllegalArgumentException -> snackbarHostState.showSnackbar(message = invalidValues)
            AppSettingsResult.NoResult -> Unit
        }

        onResetAppSettingsResult()
    }

    LaunchedEffect(key1 = shortcutResult) {
        shortcutResult?.let {
            snackbarHostState.showSnackbar(message = it)
        }

        onResetShortcutResult()
    }

    LaunchedEffect(key1 = clipboardResult) {
        clipboardResult?.let {
            snackbarHostState.showSnackbar(
                message = it,
            )
        }

        onResetClipboardResult()
    }

    LaunchedEffect(
        key1 = appSettingDialogState.selectedRadioOptionIndex,
        key2 = keyDebounce,
    ) {
        val settingType = SettingType.entries[appSettingDialogState.selectedRadioOptionIndex]

        onGetSecureSettingsByName(
            settingType,
            appSettingDialogState.key,
        )
    }

    LaunchedEffect(key1 = secureSettings) {
        appSettingDialogState.updateSecureSettings(secureSettings)
    }

    LaunchedEffect(key1 = applicationIcon) {
        applicationIcon?.let {
            addShortcutDialogState.updateIcon(it)
            updateShortcutDialogState.updateIcon(it)
        }
    }
}

@Composable
private fun AppSettingsDialogs(
    copyPermissionCommandDialogState: CopyPermissionCommandDialogState,
    appSettingDialogState: AppSettingDialogState,
    addShortcutDialogState: ShortcutDialogState,
    updateShortcutDialogState: ShortcutDialogState,
    packageName: String,
    onAddAppSetting: (AppSetting) -> Unit,
    onCopyPermissionCommand: () -> Unit,
    onAddShortcut: (MappedShortcutInfoCompat) -> Unit,
    onUpdateShortcut: (MappedShortcutInfoCompat) -> Unit,
) {
    if (appSettingDialogState.showDialog) {
        AppSettingDialog(
            appSettingDialogState = appSettingDialogState,
            packageName = packageName,
            onAddClick = onAddAppSetting,
            contentDescription = "Add App Settings Dialog",
        )
    }

    if (copyPermissionCommandDialogState.showDialog) {
        CopyPermissionCommandDialog(
            copyPermissionCommandDialogState = copyPermissionCommandDialogState,
            onCopyClick = onCopyPermissionCommand,
            contentDescription = "Copy Permission Command Dialog",
        )
    }

    if (addShortcutDialogState.showDialog) {
        AddShortcutDialog(
            shortcutDialogState = addShortcutDialogState,
            packageName = packageName,
            contentDescription = "Add Shortcut Dialog",
            onAddClick = onAddShortcut,
        )
    }

    if (updateShortcutDialogState.showDialog) {
        UpdateShortcutDialog(
            shortcutDialogState = updateShortcutDialogState,
            packageName = packageName,
            contentDescription = "Update Shortcut Dialog",
            onUpdateClick = onUpdateShortcut,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppSettingsTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigationIconClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = title, maxLines = 1)
        },
        modifier = modifier.testTag("appSettings:topAppBar"),
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = GetoIcons.Back,
                    contentDescription = "Navigation icon",
                )
            }
        },
    )
}

@Composable
private fun AppSettingsBottomAppBar(
    mappedShortcutInfoCompat: MappedShortcutInfoCompat?,
    onRevertSettingsIconClick: () -> Unit,
    onSettingsIconClick: () -> Unit,
    onAddShortcutIconClick: () -> Unit,
    onUpdateShortcutIconClick: (MappedShortcutInfoCompat) -> Unit,
    onLaunchApp: () -> Unit,
) {
    BottomAppBar(
        actions = {
            AppSettingsBottomAppBarActions(
                mappedShortcutInfoCompat = mappedShortcutInfoCompat,
                onRevertSettingsIconClick = onRevertSettingsIconClick,
                onSettingsIconClick = onSettingsIconClick,
                onAddShortcutIconClick = onAddShortcutIconClick,
                onUpdateShortcutIconClick = onUpdateShortcutIconClick,
            )
        },
        floatingActionButton = {
            AppSettingsFloatingActionButton(
                onClick = onLaunchApp,
            )
        },
    )
}

@Composable
private fun AppSettingsFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
    ) {
        Icon(
            imageVector = GetoIcons.Android,
            contentDescription = "Launch icon",
        )
    }
}

@Composable
private fun AppSettingsBottomAppBarActions(
    mappedShortcutInfoCompat: MappedShortcutInfoCompat?,
    onRevertSettingsIconClick: () -> Unit,
    onSettingsIconClick: () -> Unit,
    onAddShortcutIconClick: () -> Unit,
    onUpdateShortcutIconClick: (MappedShortcutInfoCompat) -> Unit,
) {
    IconButton(onClick = onRevertSettingsIconClick) {
        Icon(
            imageVector = GetoIcons.Refresh,
            contentDescription = "Revert icon",
        )
    }

    IconButton(onClick = onSettingsIconClick) {
        Icon(
            GetoIcons.Settings,
            contentDescription = "Settings icon",
        )
    }

    IconButton(
        onClick = {
            if (mappedShortcutInfoCompat != null) {
                onUpdateShortcutIconClick(
                    mappedShortcutInfoCompat,
                )
            } else {
                onAddShortcutIconClick()
            }
        },
    ) {
        Icon(
            GetoIcons.Shortcut,
            contentDescription = "Shortcut icon",
        )
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    text: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("appSettings:emptyListPlaceHolderScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = GetoIcons.Empty,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface,
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    GetoLoadingWheel(
        modifier = modifier,
        contentDescription = "GetoLoadingWheel",
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuccessState(
    modifier: Modifier = Modifier,
    appSettingsUiState: AppSettingsUiState.Success,
    contentPadding: PaddingValues,
    onAppSettingsItemCheckBoxChange: (Boolean, AppSetting) -> Unit,
    onDeleteAppSettingsItem: (AppSetting) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("appSettings:lazyColumn"),
        contentPadding = contentPadding,
    ) {
        items(appSettingsUiState.appSettingList, key = { it.id!! }) { appSettings ->
            AppSettingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 5.dp)
                    .animateItemPlacement(),
                appSetting = appSettings,
                onCheckAppSetting = { check ->
                    onAppSettingsItemCheckBoxChange(
                        check,
                        appSettings,
                    )
                },
                onDeleteAppSetting = {
                    onDeleteAppSettingsItem(appSettings)
                },
            )
        }
    }
}

@DevicePreviews
@Composable
private fun AppSettingsScreenLoadingStatePreview() {
    GetoTheme {
        AppSettingsScreen(
            packageName = "com.android.geto",
            appName = "Geto",
            appSettingsUiState = AppSettingsUiState.Loading,
            snackbarHostState = SnackbarHostState(),
            applicationIcon = null,
            mappedShortcutInfoCompat = null,
            secureSettings = emptyList(),
            applyAppSettingsResult = AppSettingsResult.NoResult,
            revertAppSettingsResult = AppSettingsResult.NoResult,
            shortcutResult = null,
            clipboardResult = null,
            onNavigationIconClick = {},
            onRevertAppSettings = {},
            onGetShortcut = {},
            onCheckAppSetting = { _, _ -> },
            onDeleteAppSetting = {},
            onLaunchApp = {},
            onAutoLaunchApp = {},
            onGetApplicationIcon = {},
            onResetAppSettingsResult = {},
            onResetShortcutResult = {},
            onResetClipboardResult = {},
            onGetSecureSettingsByName = { _, _ -> },
            onAddAppSetting = {},
            onCopyPermissionCommand = {},
            onAddShortcut = {},
            onUpdateShortcut = {},
        )
    }
}

@DevicePreviews
@Composable
private fun AppSettingsScreenEmptyStatePreview() {
    GetoTheme {
        AppSettingsScreen(
            packageName = "com.android.geto",
            appName = "Geto",
            appSettingsUiState = AppSettingsUiState.Success(emptyList()),
            snackbarHostState = SnackbarHostState(),
            applicationIcon = null,
            mappedShortcutInfoCompat = null,
            secureSettings = emptyList(),
            applyAppSettingsResult = AppSettingsResult.NoResult,
            revertAppSettingsResult = AppSettingsResult.NoResult,
            shortcutResult = null,
            clipboardResult = null,
            onNavigationIconClick = {},
            onRevertAppSettings = {},
            onGetShortcut = {},
            onCheckAppSetting = { _, _ -> },
            onDeleteAppSetting = {},
            onLaunchApp = {},
            onAutoLaunchApp = {},
            onGetApplicationIcon = {},
            onResetAppSettingsResult = {},
            onResetShortcutResult = {},
            onResetClipboardResult = {},
            onGetSecureSettingsByName = { _, _ -> },
            onAddAppSetting = {},
            onCopyPermissionCommand = {},
            onAddShortcut = {},
            onUpdateShortcut = {},
        )
    }
}

@DevicePreviews
@Composable
private fun AppSettingsScreenSuccessStatePreview(
    @PreviewParameter(AppSettingsPreviewParameterProvider::class) appSettings: List<AppSetting>,
) {
    GetoTheme {
        AppSettingsScreen(
            packageName = "com.android.geto",
            appName = "Geto",
            appSettingsUiState = AppSettingsUiState.Success(appSettings),
            snackbarHostState = SnackbarHostState(),
            applicationIcon = null,
            mappedShortcutInfoCompat = null,
            secureSettings = emptyList(),
            applyAppSettingsResult = AppSettingsResult.NoResult,
            revertAppSettingsResult = AppSettingsResult.NoResult,
            shortcutResult = null,
            clipboardResult = null,
            onNavigationIconClick = {},
            onRevertAppSettings = {},
            onGetShortcut = {},
            onCheckAppSetting = { _, _ -> },
            onDeleteAppSetting = {},
            onLaunchApp = {},
            onAutoLaunchApp = {},
            onGetApplicationIcon = {},
            onResetAppSettingsResult = {},
            onResetShortcutResult = {},
            onResetClipboardResult = {},
            onGetSecureSettingsByName = { _, _ -> },
            onAddAppSetting = {},
            onCopyPermissionCommand = {},
            onAddShortcut = {},
            onUpdateShortcut = {},
        )
    }
}
