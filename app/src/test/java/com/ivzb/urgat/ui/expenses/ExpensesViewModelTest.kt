package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expenses.FakeObserveExpensesUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import com.ivzb.urgat.R
import kotlinx.coroutines.flow.collect
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fakeObserveExpensesUseCase: FakeObserveExpensesUseCase
    private lateinit var viewModel: ExpensesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf(EXPENSES_FILTER_SAVED_STATE_KEY to ExpensesFilterType.ALL_EXPENSES))
        fakeObserveExpensesUseCase = FakeObserveExpensesUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Loading)
        viewModel = ExpensesViewModel(fakeObserveExpensesUseCase, savedStateHandle)

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
        val fakeExpenses = listOf(Expense("1", "Lunch"))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        viewModel = ExpensesViewModel(fakeObserveExpensesUseCase, savedStateHandle)

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
       assertEquals(fakeExpenses, state.items)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Error(R.string.error_expenses))
        viewModel = ExpensesViewModel(fakeObserveExpensesUseCase, savedStateHandle)

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
        val fakeExpenses = listOf(Expense("2", "Coffee"))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        viewModel = ExpensesViewModel(fakeObserveExpensesUseCase, savedStateHandle)

        viewModel.onIntent(ExpensesIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        // Verify flow changed — fake use case records last query
        assertEquals("coffee", fakeObserveExpensesUseCase.lastQuery)
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Success(emptyList()))
        viewModel = ExpensesViewModel(fakeObserveExpensesUseCase, savedStateHandle)

        viewModel.onIntent(ExpensesIntent.SetFiltering(ExpensesFilterType.DEBIT_EXPENSES))
        advanceUntilIdle()

        assertEquals(ExpensesFilterType.DEBIT_EXPENSES, savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY])
    }
}