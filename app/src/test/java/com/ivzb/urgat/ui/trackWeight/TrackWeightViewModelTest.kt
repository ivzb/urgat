package com.ivzb.urgat.ui.trackWeight

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.FakeCreateWeightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class TrackWeightViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var createWeightUseCase: FakeCreateWeightUseCase

    private lateinit var viewModel: TrackWeightViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        createWeightUseCase = FakeCreateWeightUseCase()
        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load weight`() = runTest {
        val uiState = viewModel.uiState.value
        assertEquals("", uiState.value)
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
        assertFalse(uiState.isWeightSaved)
    }

    @Test
    fun `saveWeight with empty fields shows message`() = runTest {
        viewModel.onIntent(TrackWeightIntent.SaveWeight)
        assertEquals(R.string.empty_weight_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveWeight creates new weight`() = runTest {
        viewModel.onIntent(TrackWeightIntent.UpdateValue("80"))
        viewModel.onIntent(TrackWeightIntent.SaveWeight)
        
        advanceUntilIdle()

        val created = createWeightUseCase.created
        assertEquals(BigDecimal("80"), created)
        assertTrue(viewModel.uiState.value.isWeightSaved)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(TrackWeightIntent.SaveWeight)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(TrackWeightIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = TrackWeightViewModel(
        createWeightUseCase,
    )
}