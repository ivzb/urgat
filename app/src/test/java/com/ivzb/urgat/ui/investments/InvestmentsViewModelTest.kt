package com.ivzb.urgat.ui.investments

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.investment.FakeObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.ui.GroupedItems
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentsViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveGroupedInvestmentsUseCase: FakeObserveGroupedInvestmentsUseCase
    private lateinit var fakeObserveInvestmentsFilteringUseCase: FakeObserveInvestmentsFilteringUseCase

    private lateinit var viewModel: InvestmentsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        savedStateHandle = SavedStateHandle(mapOf(INVESTMENTS_FILTER_SAVED_STATE_KEY to null))
        fakeObserveGroupedInvestmentsUseCase = FakeObserveGroupedInvestmentsUseCase()
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Loading)
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
        val fakeInvestments = listOf(GroupedItems("2025/12", listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))))
        val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(type = "stocks", label = "stocks", size = 1),
        )

        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
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
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Error(R.string.error_expenses))
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
        val fakeInvestments = listOf(GroupedItems("2025/12", listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))))
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(InvestmentsIntent.SearchQueryChanged("snp"))

        advanceUntilIdle()

        assertEquals("snp", fakeObserveGroupedInvestmentsUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveGroupedInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = "etfs"

        viewModel.onIntent(InvestmentsIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(
            expected,
            savedStateHandle[INVESTMENTS_FILTER_SAVED_STATE_KEY]
        )
    }

    private fun createViewModel() = InvestmentsViewModel(
        fakeObserveGroupedInvestmentsUseCase,
        fakeObserveInvestmentsFilteringUseCase,
        savedStateHandle
    )
}