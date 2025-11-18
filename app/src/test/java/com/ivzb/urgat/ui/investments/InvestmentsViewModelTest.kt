package com.ivzb.urgat.ui.investments

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsUseCase
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
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
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentsViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveInvestmentsUseCase: FakeObserveInvestmentsUseCase
    private lateinit var fakeObserveInvestmentsFilteringUseCase: FakeObserveInvestmentsFilteringUseCase

    private lateinit var viewModel: InvestmentsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf(INVESTMENTS_FILTER_SAVED_STATE_KEY to null))
        fakeObserveInvestmentsUseCase = FakeObserveInvestmentsUseCase()
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveInvestmentsUseCase.emit(Async.Loading)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.items.isEmpty())

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeInvestments = listOf(Investment("1", "SNP 500", "USD", 500.toBigDecimal(), "etfs"))
        val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(label = "stocks", size = 1),
        )

        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(fakeInvestmentsFiltering))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeInvestments, state.items)
        assertEquals(fakeInvestmentsFiltering, state.filteringUiInfo.filterMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)
        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveInvestmentsUseCase.emit(Async.Error(R.string.error_expenses))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.hasError)
        assertFalse(state.isLoading)

        job.cancel()
    }

    @Test
    fun `onSearchQueryChanged updates query and triggers new emission`() = runTest {
        val fakeInvestments = listOf(Investment("1", "SNP 500", "USD", 500.toBigDecimal(), "etfs"))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(InvestmentsIntent.SearchQueryChanged("snp"))

        advanceUntilIdle()

        assertEquals("snp", fakeObserveInvestmentsUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = FilterMenuItem(id = "etfs", label = "etfs", size = 1)

        viewModel.onIntent(InvestmentsIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[INVESTMENTS_FILTER_SAVED_STATE_KEY]
        )
    }

    @Test
    fun `header item click emits TrackWeight event`() = runTest {
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(
            InvestmentsIntent.HeaderItemClick(
                InvestmentsHeaderItem(InvestmentsHeaderItemId.Investment, Icons.Filled.AttachMoney, R.string.add_investment)
            )
        )

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(InvestmentsUiEvent.AddInvestment, event)

        job.cancel()
    }

    @Test
    fun `onEventConsumed clears uiEvent`() = runTest {
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(
            InvestmentsIntent.HeaderItemClick(
                InvestmentsHeaderItem(InvestmentsHeaderItemId.Investment, Icons.Filled.AttachMoney, R.string.add_investment)
            )
        )

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()
        assertEquals(InvestmentsUiEvent.AddInvestment, viewModel.uiState.value.uiEvent)

        viewModel.onIntent(InvestmentsIntent.EventConsumed)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.uiEvent)

        job.cancel()
    }

    private fun createViewModel() = InvestmentsViewModel(
        fakeObserveInvestmentsUseCase,
        fakeObserveInvestmentsFilteringUseCase,
        savedStateHandle
    )
}