package com.ivzb.urgat.ui.export

import android.net.Uri
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.export.FakeExportUseCase
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
class ExportViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var exportUseCase: FakeExportUseCase

    private lateinit var viewModel: ExportViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        exportUseCase = FakeExportUseCase()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init has default ui state`() = runTest {
        assertFalse(exportUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
    }

    @Test
    fun `exportData invokes exportUseCase and updates ui state`() = runTest {
        val uri = mockk<Uri>()
        viewModel.onIntent(ExportIntent.ExportData(uri))

        advanceUntilIdle()

        assertTrue(exportUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isLoading)
        assertEquals(R.string.export_data_done, uiState.userMessage)
    }

    @Test
    fun `userMessageShown invokes updates ui state`() = runTest {
        viewModel.onIntent(ExportIntent.UserMessageShown)

        val uiState = viewModel.uiState.value
        assertNull(uiState.userMessage)
        assertFalse(uiState.isLoading)
    }

    private fun createViewModel(): ExportViewModel {
        return ExportViewModel(
            exportUseCase,
        )
    }
}
