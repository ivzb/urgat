package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.expense.FakeObserveExpensesUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsUseCase
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
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModelTest {

    private lateinit var fakeObserveExpensesUseCase: FakeObserveExpensesUseCase
    private lateinit var fakeObserveInvestmentsUseCase: FakeObserveInvestmentsUseCase

    private lateinit var viewModel: FinanceViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveExpensesUseCase = FakeObserveExpensesUseCase()
        fakeObserveInvestmentsUseCase = FakeObserveInvestmentsUseCase()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertTrue(initial.expenses.isEmpty())
        assertTrue(initial.investments.isEmpty())
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeExpenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, 1L))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))

        val fakeInvestments = listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeExpenses, state.expenses)
        assertEquals(fakeInvestments, state.investments)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveExpensesUseCase.emit(Async.Error(R.string.error_expenses))

        val fakeInvestments = listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))

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
        val fakeExpenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, 1L))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveExpensesUseCase.lastQuery)
        assertEquals("coffee", fakeObserveInvestmentsUseCase.lastQuery)
    }

    @Test
    fun `header item click emits AddExpense event`() = runTest {
        `header item click emits event`(FinanceHeaderItemId.Expense, FinanceUiEvent.AddExpense)
    }

    @Test
    fun `header item click emits AddInvestment event`() = runTest {
        `header item click emits event`(FinanceHeaderItemId.Investment, FinanceUiEvent.AddInvestment)
    }

    private fun `header item click emits event`(itemId: FinanceHeaderItemId, expectedEvent: FinanceUiEvent) = runTest {
        fakeObserveExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.HeaderItemClick(itemId))

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val event = viewModel.uiState.value.uiEvent
        assertEquals(expectedEvent, event)

        job.cancel()
    }


    private fun createViewModel() = FinanceViewModel(
        fakeObserveExpensesUseCase,
        fakeObserveInvestmentsUseCase,
    )
}
