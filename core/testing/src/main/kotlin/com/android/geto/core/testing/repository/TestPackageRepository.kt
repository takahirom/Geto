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

package com.android.geto.core.testing.repository

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.android.geto.core.data.repository.PackageRepository
import com.android.geto.core.model.TargetApplicationInfo

class TestPackageRepository : PackageRepository {
    private var nonSystemAppsTestData: List<TargetApplicationInfo> = emptyList()

    override suspend fun getNonSystemApps(): List<TargetApplicationInfo> {
        return nonSystemAppsTestData
    }

    override suspend fun getApplicationIcon(packageName: String): Drawable? {
        return if (packageName in nonSystemAppsTestData.map { it.packageName }) ColorDrawable() else null
    }

    override fun getLaunchIntentForPackage(packageName: String): Intent? {
        return if (packageName in nonSystemAppsTestData.map { it.packageName }) Intent() else null
    }

    /**
     * A test-only API to add non system apps list data.
     */
    fun sendNonSystemApps(value: List<TargetApplicationInfo>) {
        nonSystemAppsTestData = value
    }
}