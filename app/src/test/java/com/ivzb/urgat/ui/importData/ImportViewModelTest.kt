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
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `importData invokes importUseCase and updates ui state`() = runTest {
        val uri = mockk<Uri>()
        viewModel.onIntent(ImportDataIntent.ImportData(uri))

        advanceUntilIdle()

        assertTrue(importUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isLoading)
        assertEquals(R.string.import_data_done, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `userMessageShown invokes updates ui state`() = runTest {
        viewModel.onIntent(ImportDataIntent.UserMessageShown)

        val uiState = viewModel.uiState.value
        assertNull(uiState.userMessage)
        assertFalse(uiState.isLoading)
        assertTrue(uiState.shouldNavigateBack)
    }

    private fun createViewModel(): ImportDataViewModel {
        return ImportDataViewModel(
            importUseCase,
        )
    }
}
