package com.ivzb.urgat.ui.inAppUpdate

import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateStatus
import com.ivzb.urgat.domain.inAppUpdate.FakeCheckForUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.FakeCompleteUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.FakeObserveInstallStateUseCase
import com.ivzb.urgat.domain.inAppUpdate.FakeStartFlexibleUpdateUseCase
import com.ivzb.urgat.util.FakeTimeProvider
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
    private lateinit var fakeTimeProvider: FakeTimeProvider

    private lateinit var viewModel: InAppUpdateViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeCheckForUpdateUseCase = FakeCheckForUpdateUseCase()
        fakeObserveInstallStateUseCase = FakeObserveInstallStateUseCase()
        fakeStartFlexibleUpdateUseCase = FakeStartFlexibleUpdateUseCase()
        fakeCompleteUpdateUseCase = FakeCompleteUpdateUseCase()
        fakeTimeProvider = FakeTimeProvider(currentTime = 0L)
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
        assertFalse(state.shouldStartUpdate)
        assertFalse(state.isDownloading)
        assertFalse(state.isDownloaded)
        assertEquals(0f, state.downloadProgress)

        job.cancel()
    }

    @Test
    fun `check for update sets shouldStartUpdate when update available`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.shouldStartUpdate)
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
        assertFalse(state.shouldStartUpdate)

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
        assertFalse(state.shouldStartUpdate)

        job.cancel()
    }

    @Test
    fun `update started consumes shouldStartUpdate`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.shouldStartUpdate)

        viewModel.onIntent(InAppUpdateIntent.UpdateStarted)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.shouldStartUpdate)

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
        assertFalse(state.shouldStartUpdate)

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
    fun `observe install state failed does not immediately re-enable shouldStartUpdate`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(InAppInstallState.Failed)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.shouldStartUpdate)
        assertFalse(state.isDownloading)

        job.cancel()
    }

    @Test
    fun `observe install state canceled does not immediately re-enable shouldStartUpdate`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        fakeObserveInstallStateUseCase.flow.emit(InAppInstallState.Canceled)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.shouldStartUpdate)
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
    fun `check for update intent triggers re-check after debounce period`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.NoUpdate
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.shouldStartUpdate)

        // Advance time past debounce
        fakeTimeProvider.currentTime = 31 * 60 * 1000L
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel.onIntent(InAppUpdateIntent.CheckForUpdate)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldStartUpdate)

        job.cancel()
    }

    @Test
    fun `check for update is debounced within 30 minutes`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.shouldStartUpdate)

        // Consume the update
        viewModel.onIntent(InAppUpdateIntent.UpdateStarted)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.shouldStartUpdate)

        // Try again within debounce window — should be ignored
        fakeTimeProvider.currentTime = 10 * 60 * 1000L
        viewModel.onIntent(InAppUpdateIntent.CheckForUpdate)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.shouldStartUpdate)

        job.cancel()
    }

    @Test
    fun `check for update works again after 30 minutes`() = runTest {
        fakeCheckForUpdateUseCase.result = InAppUpdateStatus.UpdateAvailable
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.shouldStartUpdate)

        // Consume the update
        viewModel.onIntent(InAppUpdateIntent.UpdateStarted)
        advanceUntilIdle()

        // Advance past debounce
        fakeTimeProvider.currentTime = 31 * 60 * 1000L
        viewModel.onIntent(InAppUpdateIntent.CheckForUpdate)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldStartUpdate)

        job.cancel()
    }

    private fun createViewModel() = InAppUpdateViewModel(
        checkForUpdateUseCase = fakeCheckForUpdateUseCase,
        observeInstallStateUseCase = fakeObserveInstallStateUseCase,
        startFlexibleUpdateUseCase = fakeStartFlexibleUpdateUseCase,
        completeUpdateUseCase = fakeCompleteUpdateUseCase,
        timeProvider = fakeTimeProvider,
    )
}

