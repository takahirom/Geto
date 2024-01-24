package com.feature.securesettingslist

import com.core.domain.usecase.CopySettingsUseCase
import com.core.testing.repository.TestClipboardRepository
import com.core.testing.repository.TestSecureSettingsRepository
import com.core.testing.util.MainDispatcherRule
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SecureSettingsListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var settingsRepository: TestSecureSettingsRepository

    private lateinit var clipboardRepository: TestClipboardRepository

    private lateinit var copySettingsUseCase: CopySettingsUseCase

    private lateinit var viewModel: SecureSettingsListViewModel

    @Before
    fun setUp() {
        settingsRepository = TestSecureSettingsRepository()

        clipboardRepository = TestClipboardRepository()

        copySettingsUseCase = CopySettingsUseCase(clipboardRepository)

        viewModel = SecureSettingsListViewModel(
            secureSettingsRepository = settingsRepository, copySettingsUseCase = copySettingsUseCase
        )
    }

    @Test
    fun stateIsSuccess_whenEventIsGetSecureSettingsListBySystem() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.secureSettingsListUiState.collect() }

        viewModel.onEvent(SecureSettingsListEvent.GetSecureSettingsList(0))

        testScheduler.runCurrent()

        testScheduler.advanceTimeBy(500)

        testScheduler.advanceUntilIdle()

        val item = viewModel.secureSettingsListUiState.value

        assertIs<SecureSettingsListUiState.Success>(item)

        collectJob.cancel()
    }

    @Test
    fun stateIsSuccess_whenEventIsGetSecureSettingsListBySecure() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.secureSettingsListUiState.collect() }

        viewModel.onEvent(SecureSettingsListEvent.GetSecureSettingsList(1))

        testScheduler.runCurrent()

        testScheduler.advanceTimeBy(viewModel.loadingDelay)

        testScheduler.advanceUntilIdle()

        val item = viewModel.secureSettingsListUiState.value

        assertIs<SecureSettingsListUiState.Success>(item)

        collectJob.cancel()
    }

    @Test
    fun stateIsSuccess_whenEventIsGetSecureSettingsListByGlobal() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.secureSettingsListUiState.collect() }

        viewModel.onEvent(SecureSettingsListEvent.GetSecureSettingsList(2))

        testScheduler.runCurrent()

        testScheduler.advanceTimeBy(500)

        testScheduler.advanceUntilIdle()

        val item = viewModel.secureSettingsListUiState.value

        assertIs<SecureSettingsListUiState.Success>(item)

        collectJob.cancel()
    }

    @Test
    fun snackBarNotNull_whenEventIsOnCopySecureSettingsOnAndroidBelow12() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.secureSettingsListUiState.collect() }

        clipboardRepository.setAndroidTwelveBelow(true)

        viewModel.onEvent(SecureSettingsListEvent.OnCopySecureSettingsList("Hi"))

        assertNotNull(viewModel.snackBar.value)

        collectJob.cancel()
    }

    @Test
    fun snackBarNull_whenEventIsOnCopySecureSettingsOnAndroidAbove12() = runTest {
        clipboardRepository.setAndroidTwelveBelow(false)

        viewModel.onEvent(SecureSettingsListEvent.OnCopySecureSettingsList("Hi"))

        assertNull(viewModel.snackBar.value)
    }
}