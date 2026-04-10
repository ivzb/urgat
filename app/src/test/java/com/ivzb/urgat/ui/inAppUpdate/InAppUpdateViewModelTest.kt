package com.ivzb.urgat.ui.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateStatus
import com.ivzb.urgat.domain.inAppUpdate.FakeCheckForUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.FakeCompleteUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.FakeObserveInstallStateUseCase
import com.ivzb.urgat.domain.inAppUpdate.FakeStartFlexibleUpdateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class InAppUpdateViewModelTest {

    private lateinit var fakeCheckForUpdateUseCase: FakeCheckForUpdateUseCase
    private lateinit var fakeObserveInstallStateUseCase: FakeObserveInstallStateUseCase
    private lateinit var fakeStartFlexibleUpdateUseCase: FakeStartFlexibleUpdateUseCase
    private lateinit var fakeCompleteUpdateUseCase: FakeCompleteUpdateUseCase

    private lateinit var viewModel: InAppUpdateViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeCheckForUpdateUseCase = FakeCheckForUpdateUseCase()
        fakeObserveInstallStateUseCase = FakeObserveInstallStateUseCase()
        fakeStartFlexibleUpdateUseCase = FakeStartFlexibleUpdateUseCase()
        fakeCompleteUpdateUseCase = FakeCompleteUpdateUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isUpdateAvailable)
        assertFalse(state.isDownloading)
        assertFalse(state.isDownloaded)
        assertFalse(state.isDismissed)
        assertEquals(0f, state.downloadProgress)

        job.cancel()
    }

    @Test
    fun `check for update sets isUpdateAvailable when update available`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isUpdateAvailable)
        assertFalse(state.isDownloading)
        assertFalse(state.isDownloaded)

        job.cancel()
    }

    @Test
    fun `check for update sets isDownloaded when already downloaded`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.Downloaded
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isDownloaded)
        assertFalse(state.isDownloading)
        assertFalse(state.isUpdateAvailable)

        job.cancel()
    }

    @Test
    fun `check for update sets isDownloading when in progress`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.InProgress
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isDownloading)
        assertFalse(state.isDownloaded)
        assertFalse(state.isUpdateAvailable)

        job.cancel()
    }

    @Test
    fun `dismiss update sets isDismissed`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isUpdateAvailable)

        viewModel.onIntent(InAppUpdateIntent.DismissUpdate)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isDismissed)

        job.cancel()
    }

    @Test
    fun `complete update calls completeUpdateUseCase`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.Downloaded
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        viewModel.onIntent(InAppUpdateIntent.CompleteUpdate)
        advanceUntilIdle()

        assertTrue(fakeCompleteUpdateUseCase.completed)

        job.cancel()
    }

    @Test
    fun `observe install state downloading updates progress`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(
            InAppInstallState.Downloading(bytesDownloaded = 50, totalBytesToDownload = 100)
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isDownloading)
        assertEquals(0.5f, state.downloadProgress)
        assertFalse(state.isUpdateAvailable)

        job.cancel()
    }

    @Test
    fun `observe install state downloaded sets isDownloaded`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(InAppInstallState.Downloaded)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isDownloaded)
        assertFalse(state.isDownloading)
        assertFalse(state.isDismissed)

        job.cancel()
    }

    @Test
    fun `observe install state installed resets state`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.Downloaded
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isDownloaded)

        fakeObserveInstallStateUseCase.flow.emit(InAppInstallState.Installed)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(InAppUpdateUiState(), state)

        job.cancel()
    }

    @Test
    fun `observe install state failed re-enables update available`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(InAppInstallState.Failed)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isUpdateAvailable)
        assertFalse(state.isDownloading)

        job.cancel()
    }

    @Test
    fun `observe install state canceled re-enables update available`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(InAppInstallState.Canceled)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isUpdateAvailable)
        assertFalse(state.isDownloading)

        job.cancel()
    }

    @Test
    fun `downloading with zero total bytes sets progress to zero`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(
            InAppInstallState.Downloading(bytesDownloaded = 0, totalBytesToDownload = 0)
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isDownloading)
        assertEquals(0f, state.downloadProgress)

        job.cancel()
    }

    @Test
    fun `check for update intent triggers re-check`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isUpdateAvailable)

        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel.onIntent(InAppUpdateIntent.CheckForUpdate)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isUpdateAvailable)

        job.cancel()
    }

    private fun createViewModel() = InAppUpdateViewModel(
        checkForUpdateUseCase = fakeCheckForUpdateUseCase,
        observeInstallStateUseCase = fakeObserveInstallStateUseCase,
        startFlexibleUpdateUseCase = fakeStartFlexibleUpdateUseCase,
        completeUpdateUseCase = fakeCompleteUpdateUseCase,
    )
}

