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

package com.android.geto.feature.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.android.geto.core.model.DarkThemeConfig
import com.android.geto.core.model.ThemeBrand
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun getoLoadingWheel_isDisplayed_whenSettingsUiState_isLoading() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Loading,
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("GetoLoadingWheel").assertIsDisplayed()
    }

    @Test
    fun settings_isDisplayed_whenSettingsUiState_isSuccess() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.DEFAULT,
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:success").assertIsDisplayed()
    }

    @Test
    fun dynamicColorSwitch_isOn_whenUseDynamicColor_isTrue() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.DEFAULT,
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                supportDynamicColor = true,
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:dynamicSwitch").assertIsOn()
    }

    @Test
    fun dynamicColorSwitch_isOff_whenUseDynamicColor_isFalse() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.DEFAULT,
                        useDynamicColor = false,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                supportDynamicColor = true,
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:dynamicSwitch").assertIsOff()
    }

    @Test
    fun dynamicRow_isDisplayed_whenThemeBrand_isDefault() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.DEFAULT,
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                supportDynamicColor = true,
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:dynamic").assertIsDisplayed()
    }

    @Test
    fun dynamicRow_isNotDisplayed_whenThemeBrand_isAndroid() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.ANDROID,
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                supportDynamicColor = true,
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:dynamic").assertIsNotDisplayed()
    }

    @Test
    fun dynamicRow_isNotDisplayed_whenUnSupportDynamicColor() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.ANDROID,
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                supportDynamicColor = false,
                onThemeDialog = {},
                onDarkDialog = {},
                onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:dynamic").assertIsNotDisplayed()
    }

    @Test
    fun autoLaunchSwitch_isOff_whenUseAutoLaunch_isFalse() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.DEFAULT,
                        useDynamicColor = false,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = false,
                    ),
                ),
                supportDynamicColor = true,
                onThemeDialog = {},
                onDarkDialog = {}, onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:autoLaunchSwitch").assertIsOff()
    }

    @Test
    fun autoLaunchSwitch_isOn_whenUseAutoLaunch_isTrue() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = SettingsUiState.Success(
                    UserEditableSettings(
                        brand = ThemeBrand.DEFAULT,
                        useDynamicColor = false,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        useAutoLaunch = true,
                    ),
                ),
                supportDynamicColor = true,
                onThemeDialog = {},
                onDarkDialog = {}, onCleanDialog = {},
                onChangeDynamicColorPreference = {},
                onChangeAutoLaunchPreference = {},
                onNavigationIconClick = {},
            )
        }

        composeTestRule.onNodeWithTag("settings:autoLaunchSwitch").assertIsOn()
    }
}