package com.ivzb.urgat.ui.sample

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.sample.FakeSampleUseCase
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
class SampleViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var sampleUseCase: FakeSampleUseCase

    private lateinit var viewModel: SampleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        sampleUseCase = FakeSampleUseCase()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init has default ui state`() = runTest {
        assertFalse(sampleUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `addSampleData invokes sampleUseCase and updates ui state`() = runTest {
        viewModel.onIntent(SampleIntent.AddSampleData)

        advanceUntilIdle()

        assertTrue(sampleUseCase.invoked)
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isLoading)
        assertEquals(R.string.sample_data_added, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `userMessageShown invokes updates ui state`() = runTest {
        viewModel.onIntent(SampleIntent.UserMessageShown)

        val uiState = viewModel.uiState.value
        assertNull(uiState.userMessage)
        assertFalse(uiState.isLoading)
        assertTrue(uiState.shouldNavigateBack)
    }

    private fun createViewModel(): SampleViewModel {
        return SampleViewModel(
            sampleUseCase,
        )
    }
}
