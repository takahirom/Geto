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

package com.android.geto.core.data.testdoubles

import com.android.geto.core.database.dao.AppSettingsDao
import com.android.geto.core.database.model.AppSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet

class TestAppSettingsDao : AppSettingsDao {
    private val _appSettingsFlow = MutableStateFlow<MutableList<AppSettingsEntity>>(mutableListOf())

    override suspend fun upsert(entity: AppSettingsEntity): Long {
        val result = _appSettingsFlow.updateAndGet { updatedList ->
            val existingEntityIndex = updatedList.indexOfFirst { it.id == entity.id }

            if (existingEntityIndex != -1) {
                updatedList[existingEntityIndex] = entity

            } else {
                updatedList.add(entity)
            }

            updatedList
        }

        return if (entity in result) 1L else 0L
    }

    override suspend fun delete(entity: AppSettingsEntity): Int {
        val result = _appSettingsFlow.updateAndGet { updatedList ->
            updatedList.removeIf { it.id == entity.id }
            updatedList
        }

        return if (entity !in result) 1 else 0
    }

    override fun getAppSettingsList(packageName: String): Flow<List<AppSettingsEntity>> {
        return _appSettingsFlow.asStateFlow().map { entities ->
            entities.filter { it.packageName == packageName }
        }
    }
}