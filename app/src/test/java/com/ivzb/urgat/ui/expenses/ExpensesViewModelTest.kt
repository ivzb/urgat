package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.domain.expense.FakeObserveExpensesUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.FakeObserveExpensesFilteringUseCase
import com.ivzb.urgat.ui.components.FilterMenuItem
import kotlinx.coroutines.flow.collect
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveExpensesUseCase: FakeObserveExpensesUseCase
    private lateinit var fakeObserveExpensesFilteringUseCase: FakeObserveExpensesFilteringUseCase

    private lateinit var viewModel: ExpensesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf(EXPENSES_FILTER_SAVED_STATE_KEY to null))
        fakeObserveExpensesUseCase = FakeObserveExpensesUseCase()
        fakeObserveExpensesFilteringUseCase = FakeObserveExpensesFilteringUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Loading)
        fakeObserveExpensesFilteringUseCase.emit(Async.Loading)
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
        val fakeExpenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, 1L))
        val fakeExpensesFiltering = listOf(
            FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 1),
            FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 0),
        )

        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(fakeExpensesFiltering))
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeExpenses, state.items)
        assertEquals(fakeExpensesFiltering, state.filteringUiInfo.filterMenuItems)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Error(R.string.error_expenses))
        fakeObserveExpensesFilteringUseCase.emit(Async.Error(R.string.error_expenses_filtering))
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
        val fakeExpenses = listOf(Expense("2", "Coffee", BigDecimal.TEN, "food", 1L, false, 1L))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(ExpensesIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveExpensesUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Success(emptyList()))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        val expected = FilterMenuItem<ExpensesFilterType>(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 1)

        viewModel.onIntent(ExpensesIntent.SetFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY])
    }

    private fun createViewModel() = ExpensesViewModel(
        fakeObserveExpensesUseCase,
        fakeObserveExpensesFilteringUseCase,
        savedStateHandle
    )
}