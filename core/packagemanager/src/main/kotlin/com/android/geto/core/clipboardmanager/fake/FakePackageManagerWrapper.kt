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

package com.android.geto.core.clipboardmanager.fake

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.android.geto.core.clipboardmanager.PackageManagerWrapper
import com.android.geto.core.model.TargetApplicationInfo
import javax.inject.Inject

class FakePackageManagerWrapper @Inject constructor() : PackageManagerWrapper {

    private var _installedApplications = listOf(
        TargetApplicationInfo(
            flags = 0, packageName = "com.android.apptest0", label = "Application 0"
        ), TargetApplicationInfo(
            flags = 0, packageName = "com.android.apptest1", label = "Application 1"
        ), TargetApplicationInfo(
            flags = 0, packageName = "com.android.apptest2", label = "Application 2"
        ), TargetApplicationInfo(
            flags = 0, packageName = "com.android.apptest3", label = "Application 3"
        ), TargetApplicationInfo(
            flags = 0, packageName = "com.android.apptest4", label = "Application 4"
        )
    )

    override fun getInstalledApplications(): List<TargetApplicationInfo> {
        return _installedApplications
    }

    @Throws(PackageManager.NameNotFoundException::class)
    override fun getApplicationIcon(packageName: String): Drawable {
        throw PackageManager.NameNotFoundException()
    }

    override fun getLaunchIntentForPackage(packageName: String): Intent? {
        return null
    }
}