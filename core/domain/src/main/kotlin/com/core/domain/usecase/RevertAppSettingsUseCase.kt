package com.core.domain.usecase

import com.core.domain.repository.AppSettingsRepository
import com.core.domain.repository.SecureSettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RevertAppSettingsUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val secureSettingsRepository: SecureSettingsRepository
) {

    suspend operator fun invoke(
        packageName: String,
        onEmptyAppSettingsList: (String) -> Unit,
        onAppSettingsDisabled: (String) -> Unit,
        onAppSettingsNotSafeToWrite: (String) -> Unit,
        onReverted: (String) -> Unit,
        onSecurityException: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {

        val appSettingsList = appSettingsRepository.getAppSettingsList(packageName).first()

        if (appSettingsList.isEmpty()) {

            onEmptyAppSettingsList("No settings found")

            return
        }

        if (appSettingsList.any { !it.enabled }) {

            onAppSettingsDisabled("Please enable atleast one setting")

            return
        }

        if (appSettingsList.any { !it.safeToWrite }) {

            onAppSettingsNotSafeToWrite("Reverting settings that don't exist is not allowed. Please remove items highlighted as red")

            return
        }

        secureSettingsRepository.revertSecureSettings(appSettingsList).onSuccess { reverted ->
            if (reverted) {
                onReverted("Settings reverted")
            } else {
                onFailure("Database failure")
            }
        }.onFailure { t ->
            if (t is SecurityException) {
                onSecurityException("Permission not granted")
            } else {
                onFailure(t.localizedMessage)
            }
        }
    }
}