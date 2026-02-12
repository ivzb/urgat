package com.ivzb.urgat.ui.finance

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.FakeObserveDashboardBudgetUseCase
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.FakeObserveExpensesUiStateUseCase
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.fire.FakeObserveFiReUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsUseCase
import com.ivzb.urgat.domain.saving.FakeObserveSavingsUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.util.Async
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
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

    private lateinit var fakeObserveDashboardBudgetUseCase: FakeObserveDashboardBudgetUseCase
    private lateinit var fakeObserveExpensesUiStateUseCase: FakeObserveExpensesUiStateUseCase
    private lateinit var fakeObserveInvestmentsUseCase: FakeObserveInvestmentsUseCase
    private lateinit var fakeObserveSavingsUseCase: FakeObserveSavingsUseCase
    private lateinit var fakeObserveBudgetUseCase: FakeObserveBudgetsUseCase
    private lateinit var fakeObserveFiReUseCase: FakeObserveFiReUseCase
    private lateinit var mockFireRepository: FireRepository
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: FinanceViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveDashboardBudgetUseCase = FakeObserveDashboardBudgetUseCase()
        fakeObserveExpensesUiStateUseCase = FakeObserveExpensesUiStateUseCase()
        fakeObserveInvestmentsUseCase = FakeObserveInvestmentsUseCase()
        fakeObserveSavingsUseCase = FakeObserveSavingsUseCase()
        fakeObserveBudgetUseCase = FakeObserveBudgetsUseCase()
        fakeObserveFiReUseCase = FakeObserveFiReUseCase()
        mockFireRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf())

        every { mockFireRepository.withdrawalRateFlow } returns flowOf(4.toBigDecimal())
        every { mockFireRepository.returnOfInvestmentFlow } returns flowOf(7.toBigDecimal())
        every { mockFireRepository.taxRateFlow } returns flowOf(10.toBigDecimal())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveDashboardBudgetUseCase.emit(Async.Loading)
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertNull(initial.dashboardBudget)
        assertEquals(initial.expensesUiState, ExpensesUiState())
        assertTrue(initial.investments.isEmpty())
        assertTrue(initial.savings.isEmpty())
        assertTrue(initial.budgets.isEmpty())
        assertNull(initial.fire)
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeDashboardBudget = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "",
                total = 5000.toBigDecimal(),
                items = listOf(
                    BudgetCategory("", "", 5000.toBigDecimal()),
                ),
            ),
            totalIncome = 5000.toBigDecimal(),
            totalExpenses = 2600.toBigDecimal(),
            totalInvestments = 1300.toBigDecimal(),
            totalInvestmentsFormatted = "1300 EUR",
            totalSavings = 100.toBigDecimal(),
        )
        fakeObserveDashboardBudgetUseCase.emit(Async.Success(fakeDashboardBudget))

        val fakeExpensesUiState = ExpensesUiState(
            expenses = listOf(GroupedExpenses("1", 10.toBigDecimal(), listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))))
        )
        fakeObserveExpensesUiStateUseCase.emit(Async.Success(fakeExpensesUiState))

        val fakeInvestments = listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))

        val fakeSavings = listOf(Saving("1", "Emergency fund", 500.toBigDecimal(), 1L, false, 1L))
        fakeObserveSavingsUseCase.emit(Async.Success(fakeSavings))

        val fakeBudgets = listOf(Budget("1", 5000.toBigDecimal(), 9500.toBigDecimal(), 11300.toBigDecimal(), 1L, false, 1L))
        fakeObserveBudgetUseCase.emit(Async.Success(fakeBudgets))

        val fakeFiRe = FiRe(
            selection = "Dedember 2025",

            monthlyIncome = 5000.toBigDecimal(),
            monthlyExpenses = 3000.toBigDecimal(),
            monthlyInvestments = 2000.toBigDecimal(),

            annualIncome = 60_000.toBigDecimal(),
            annualExpenses = 36_000.toBigDecimal(),
            annualInvestments = 24_000.toBigDecimal(),

            accumulatedStash = 50_000.toBigDecimal(),
            savingsRate = 40.toBigDecimal(),
            target = 990_000.toBigDecimal(),

            withdrawalRate = 4.toBigDecimal(),
            returnOfInvestment = 7.toBigDecimal(),
            taxRate = 10.toBigDecimal(),

            yearsUntilFiRe = 17.7.toBigDecimal(),
        )
        fakeObserveFiReUseCase.emit(Async.Success(fakeFiRe))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeDashboardBudget, state.dashboardBudget)
        assertEquals(fakeExpensesUiState, state.expensesUiState)
        assertEquals(fakeInvestments, state.investments)
        assertEquals(fakeSavings, state.savings)
        assertEquals(fakeBudgets, state.budgets)
        assertEquals(fakeFiRe, state.fire)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveDashboardBudgetUseCase.emit(Async.Error)
        fakeObserveExpensesUiStateUseCase.emit(Async.Error)

        val fakeInvestments = listOf(Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), "etfs", "USD", 1L, false, 1L))
        fakeObserveInvestmentsUseCase.emit(Async.Success(fakeInvestments))

        val fakeSavings = listOf(Saving("1", "Emergency fund", 500.toBigDecimal(), 1L, false, 1L))
        fakeObserveSavingsUseCase.emit(Async.Success(fakeSavings))

        val fakeBudgets = listOf(Budget("1", 5000.toBigDecimal(), 9500.toBigDecimal(), 11300.toBigDecimal(), 1L, false, 1L))
        fakeObserveBudgetUseCase.emit(Async.Success(fakeBudgets))

        val fakeFiRe = FiRe(
            selection = "December 2025",

            monthlyIncome = 5000.toBigDecimal(),
            monthlyExpenses = 3000.toBigDecimal(),
            monthlyInvestments = 2000.toBigDecimal(),

            annualIncome = 60_000.toBigDecimal(),
            annualExpenses = 36_000.toBigDecimal(),
            annualInvestments = 24_000.toBigDecimal(),

            accumulatedStash = 50_000.toBigDecimal(),
            savingsRate = 40.toBigDecimal(),
            target = 990_000.toBigDecimal(),

            withdrawalRate = 4.toBigDecimal(),
            returnOfInvestment = 7.toBigDecimal(),
            taxRate = 10.toBigDecimal(),

            yearsUntilFiRe = 17.7.toBigDecimal(),
        )
        fakeObserveFiReUseCase.emit(Async.Success(fakeFiRe))

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
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SearchQueryChanged("coffee"))

        advanceUntilIdle()

        assertEquals("coffee", fakeObserveDashboardBudgetUseCase.lastQuery)
        assertEquals("coffee", fakeObserveExpensesUiStateUseCase.lastQuery)
        assertEquals("coffee", fakeObserveInvestmentsUseCase.lastQuery)
        assertEquals("coffee", fakeObserveSavingsUseCase.lastQuery)
        assertEquals("coffee", fakeObserveBudgetUseCase.lastQuery)
        assertEquals("coffee", fakeObserveFiReUseCase.lastQuery)
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
        fakeObserveDashboardBudgetUseCase.emit(Async.Success(null))
        fakeObserveExpensesUiStateUseCase.emit(Async.Success(ExpensesUiState()))
        fakeObserveInvestmentsUseCase.emit(Async.Success(listOf()))
        fakeObserveSavingsUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetUseCase.emit(Async.Success(listOf()))
        fakeObserveFiReUseCase.emit(Async.Success(null))
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

    @Test
    fun `updateFireWithdrawalRate intent updates fire repository`() = runTest {
        coEvery { mockFireRepository.updateWithdrawalRate(any()) } just Runs

        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.UpdateFireWithdrawalRate("4"))

        advanceUntilIdle()

        coVerify(exactly = 1) { mockFireRepository.updateWithdrawalRate(eq("4")) }
    }

    @Test
    fun `updateReturnOfInvestment intent updates fire repository`() = runTest {
        coEvery { mockFireRepository.updateReturnOfInvestment(any()) } just Runs

        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.UpdateFireReturnOfInvestment("7"))

        advanceUntilIdle()

        coVerify(exactly = 1) { mockFireRepository.updateReturnOfInvestment(eq("7")) }
    }

    @Test
    fun `updateTaxRate intent updates fire repository`() = runTest {
        coEvery { mockFireRepository.updateTaxRate(any()) } just Runs

        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.UpdateFireTaxRate("7"))

        advanceUntilIdle()

        coVerify(exactly = 1) { mockFireRepository.updateTaxRate(eq("7")) }
    }

    @Test
    fun `setFiltering updates saved state handle`() = runTest {
        viewModel = createViewModel()

        val expected = ExpensesFilterType.DEBIT_EXPENSES

        viewModel.onIntent(FinanceIntent.SetExpensesFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, savedStateHandle[FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY])
    }

    @Test
    fun `setSorting updates saved state handle`() = runTest {
        viewModel = createViewModel()

        val expected = ExpensesSortType.BY_MIN

        viewModel.onIntent(FinanceIntent.SetExpensesSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, savedStateHandle[FINANCE_EXPENSES_SORT_SAVED_STATE_KEY])
    }

    private fun createViewModel() = FinanceViewModel(
        fakeObserveDashboardBudgetUseCase,
        fakeObserveExpensesUiStateUseCase,
        fakeObserveInvestmentsUseCase,
        fakeObserveSavingsUseCase,
        fakeObserveBudgetUseCase,
        fakeObserveFiReUseCase,
        mockFireRepository,
        savedStateHandle,
    )
}
