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
package com.android.geto.core.domain

import com.android.geto.core.data.repository.ShortcutRepository
import com.android.geto.core.model.MappedShortcutInfoCompat
import javax.inject.Inject

class RequestPinShortcutUseCase @Inject constructor(private val shortcutRepository: ShortcutRepository) {
    operator fun invoke(
        packageName: String,
        appName: String,
        mappedShortcutInfoCompat: MappedShortcutInfoCompat,
    ): RequestPinShortcutResult {
        if (shortcutRepository.isRequestPinShortcutSupported().not()) {
            return RequestPinShortcutResult.UnSupportedLauncher
        }

        return if (shortcutRepository.requestPinShortcut(
                packageName = packageName,
                appName = appName,
                mappedShortcutInfoCompat = mappedShortcutInfoCompat,
            )
        ) {
            RequestPinShortcutResult.SupportedLauncher
        } else {
            RequestPinShortcutResult.UnSupportedLauncher
        }
    }
}

sealed interface RequestPinShortcutResult {
    data object UnSupportedLauncher : RequestPinShortcutResult

    data object SupportedLauncher : RequestPinShortcutResult
}
