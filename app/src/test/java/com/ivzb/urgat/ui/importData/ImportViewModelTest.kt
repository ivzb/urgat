package com.ivzb.urgat.ui.importData

import android.net.Uri
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.importData.FakeImportUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ImportViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var importUseCase: FakeImportUseCase

    private lateinit var viewModel: ImportDataViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        importUseCase = FakeImportUseCase()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init has default ui state`() = runTest {
        assertFalse(importUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isManualMode)
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
        assertTrue(uiState.manualDataContent.isEmpty())
    }

    @Test
    fun `importDataFolder invokes importUseCase and updates ui state`() = runTest {
        val uri = mockk<Uri>()
        viewModel.onIntent(ImportDataIntent.ImportDataFolder(uri))

        advanceUntilIdle()

        assertTrue(importUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isManualMode)
        assertFalse(uiState.isLoading)
        assertEquals(R.string.import_data_done, uiState.userMessage)
    }

    @Test
    fun `switchToManualMode updates ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.SwitchToManualMode)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertTrue(uiState.isManualMode)
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
    }

    @Test
    fun `updateContent updates ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.UpdateContent(ImportDataPage.Expenses, "test"))

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.manualDataContent.size)
        assertEquals("test", uiState.manualDataContent[ImportDataPage.Expenses])
        assertFalse(uiState.isManualMode)
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
    }

    @Test
    fun `updateContent twice updates only last value to ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.UpdateContent(ImportDataPage.Expenses, "test"))
        viewModel.onIntent(ImportDataIntent.UpdateContent(ImportDataPage.Investments, "invest"))

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.manualDataContent.size)
        assertEquals("invest", uiState.manualDataContent[ImportDataPage.Investments])
        assertFalse(uiState.isManualMode)
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
    }

    @Test
    fun `importDataManually with no content shows error message`() = runTest {
        viewModel.onIntent(ImportDataIntent.ImportDataManually(ImportDataPage.Expenses))

        advanceUntilIdle()

        assertFalse(importUseCase.invoked)
        assertFalse(importUseCase.invokedExpenses)

        val uiState = viewModel.uiState.value
        assertTrue(uiState.manualDataContent.isEmpty())
        assertFalse(uiState.isLoading)
        assertEquals(R.string.import_data_failed, uiState.userMessage)
    }

    @Test
    fun `importDataManually invokes importUseCase and updates ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.UpdateContent(ImportDataPage.Expenses, "test"))
        viewModel.onIntent(ImportDataIntent.ImportDataManually(ImportDataPage.Expenses))

        advanceUntilIdle()

        assertFalse(importUseCase.invoked)
        assertTrue(importUseCase.invokedExpenses)

        val uiState = viewModel.uiState.value
        assertTrue(uiState.manualDataContent.isEmpty())
        assertFalse(uiState.isLoading)
        assertEquals(R.string.import_data_done, uiState.userMessage)
    }

    @Test
    fun `importDataManually carMaintenance invokes importUseCase and updates ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.UpdateContent(ImportDataPage.CarMaintenance, "test"))
        viewModel.onIntent(ImportDataIntent.ImportDataManually(ImportDataPage.CarMaintenance))

        advanceUntilIdle()

        assertFalse(importUseCase.invoked)
        assertTrue(importUseCase.invokedCarMaintenance)

        val uiState = viewModel.uiState.value
        assertTrue(uiState.manualDataContent.isEmpty())
        assertFalse(uiState.isLoading)
        assertEquals(R.string.import_data_done, uiState.userMessage)
    }

    @Test
    fun `userMessageShown invokes updates ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.UserMessageShown)

        val uiState = viewModel.uiState.value
        assertNull(uiState.userMessage)
        assertFalse(uiState.isLoading)
    }

    private fun createViewModel(): ImportDataViewModel {
        return ImportDataViewModel(
            importUseCase,
        )
    }
}
