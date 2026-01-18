package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUseCase
import com.ivzb.urgat.domain.expense.FakeObserveExpensesUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsUseCase
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
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
    private lateinit var fakeObserveBudgetUseCase: FakeObserveBudgetsUseCase
    private lateinit var mockResourcesProvider: ResourcesProvider

    private lateinit var viewModel: FinanceViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveExpensesUseCase = FakeObserveExpensesUseCase()
        fakeObserveInvestmentsUseCase = FakeObserveInvestmentsUseCase()
        fakeObserveBudgetUseCase = FakeObserveBudgetsUseCase()
        mockResourcesProvider = mockk()

        every { mockResourcesProvider.getInteger(R.integer.items_limit) } returns 5
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
        val fakeExpenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))

        val fakeInvestments = listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))

        val fakeBudgets = listOf(Budget("1", 5000.toBigDecimal(), 9500.toBigDecimal(), 11300.toBigDecimal(), 1L, false, 1L))
        fakeObserveBudgetUseCase.emit(Async.Success(fakeBudgets))

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
        fakeObserveExpensesUseCase.emit(Async.Error)

        val fakeInvestments = listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))

        val fakeBudgets = listOf(Budget("1", 5000.toBigDecimal(), 9500.toBigDecimal(), 11300.toBigDecimal(), 1L, false, 1L))
        fakeObserveBudgetUseCase.emit(Async.Success(fakeBudgets))

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
        val fakeExpenses = listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))
        fakeObserveExpensesUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveExpensesUseCase.lastQuery)
        assertEquals("coffee", fakeObserveInvestmentsUseCase.lastQuery)
    }

    @Test
    fun `fab action click emits AddExpense event`() = runTest {
        `fab action click emits event`(FinanceFabActionId.Expense, FinanceUiEvent.AddExpense)
    }

    @Test
    fun `fab action click emits AddInvestment event`() = runTest {
        `fab action click emits event`(FinanceFabActionId.Investment, FinanceUiEvent.AddInvestment)
    }

    private fun `fab action click emits event`(fabActionId: FinanceFabActionId, expectedEvent: FinanceUiEvent) = runTest {
        fakeObserveExpensesUseCase.emit(Async.Success(listOf()))
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetUseCase.emit(Async.Success(listOf()))
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.FabActionClick(fabActionId))

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
        fakeObserveBudgetUseCase,
        mockResourcesProvider,
    )
}
