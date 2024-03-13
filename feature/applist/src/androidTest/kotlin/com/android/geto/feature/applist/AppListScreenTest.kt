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

package com.android.geto.feature.applist

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.geto.core.model.TargetApplicationInfo
import org.junit.Rule
import org.junit.Test

class AppListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingPlaceHolderScreenIsDisplayed_whenAppListUiStateIsLoading() {
        composeTestRule.setContent {
            AppListScreen(appListUiState = AppListUiState.Loading,
                          onItemClick = { _, _ -> },
                          onSettingsClick = {})
        }

        composeTestRule.onNodeWithTag("applist:loadingPlaceHolderScreen").assertIsDisplayed()
    }

    @Test
    fun lazyColumnIsDisplayed_whenAppListUiStateIsSuccess() {
        composeTestRule.setContent {
            AppListScreen(appListUiState = AppListUiState.Success(testTargetApplicationInfoLists),
                          onItemClick = { _, _ -> },
                          onSettingsClick = {})
        }

        composeTestRule.onNodeWithTag("applist:lazyColumn").assertIsDisplayed()
    }
}

private val testTargetApplicationInfoLists = List(2) { index ->
    val icon = ShapeDrawable(OvalShape()).apply {
        paint.color = Color.GRAY

        setBounds(0, 0, (50 * 2), (50 * 2))
    }

    TargetApplicationInfo(
        flags = 0, icon = icon, packageName = "packageName$index", label = "Label $index"
    )
}