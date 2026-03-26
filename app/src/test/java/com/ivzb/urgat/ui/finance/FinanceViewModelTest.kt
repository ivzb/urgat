package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.finance.FakeFinanceRepository
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.finance.FakeObserveFinanceUiStateUseCase
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.savings.SavingsUiState
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

    private lateinit var fakeObserveFinanceUiStateUseCase: FakeObserveFinanceUiStateUseCase
    private lateinit var mockFireRepository: FireRepository
    private lateinit var fakeFinanceRepository: FakeFinanceRepository

    private lateinit var viewModel: FinanceViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeObserveFinanceUiStateUseCase = FakeObserveFinanceUiStateUseCase()
        mockFireRepository = mockk()
        fakeFinanceRepository = FakeFinanceRepository()

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
        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        val initial = viewModel.uiState.value
        assertTrue(initial.isLoading)
        assertFalse(initial.hasError)
        assertNull(initial.reportBudget)
        assertEquals(initial.expensesUiState, ExpensesUiState())
        assertEquals(initial.investmentsUiState, InvestmentsUiState())
        assertEquals(initial.savingsUiState, SavingsUiState())
        assertEquals(initial.budgetsUiState, BudgetsUiState())
        assertNull(initial.fire)
        assertNull(initial.uiEvent)

        job.cancel()
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        val fakeReportBudget = GroupedBudget(
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
            totalInvestmentsFormatted = "1,300 EUR",
            totalSavings = 100.toBigDecimal(),
        )

        val fakeExpensesUiState = ExpensesUiState(
            expenses = listOf(GroupedExpenses("1", 10.toBigDecimal(), listOf(Expense("1", "Lunch", BigDecimal.TEN, "food", 1L, false, false, 1L))))
        )

        val fakeInvestmentsUiState = InvestmentsUiState(
            investments = listOf(
                GroupedInvestments(
                    group = "1",
                    total = listOf(
                        InvestmentTotal(
                            currency = "USD",
                            costPrice = BigDecimal("150000"),
                            marketPrice = BigDecimal.ZERO,
                            pnl = BigDecimal("-100"),
                        ),
                    ),
                    items = listOf(
                        Investment("1", "SNP 500", 500.toBigDecimal(), 300.toBigDecimal(), BigDecimal.ZERO, "etfs", "USD", 1L, false, 1L),
                    ),
                ),
            ),
        )

        val fakeSavingsUiState = SavingsUiState(
            savings = listOf(GroupedSavings("1", 500.toBigDecimal(), listOf(Saving("1", "Emergency fund", 500.toBigDecimal(), 1L, false, 1L))))
        )

        val fakeBudgetsUiState = BudgetsUiState(
            budgets = listOf(Budget("1", 5000.toBigDecimal(), 9500.toBigDecimal(), 11300.toBigDecimal(), "EUR", 1L, false, 1L))
        )

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

        fakeObserveFinanceUiStateUseCase.emit(Async.Success(FinanceUiState(
            expensesUiState = fakeExpensesUiState,
            investmentsUiState = fakeInvestmentsUiState,
            savingsUiState = fakeSavingsUiState,
            budgetsUiState = fakeBudgetsUiState,
            reportBudget = fakeReportBudget,
            fire = fakeFiRe,
        )))

        viewModel = createViewModel()

        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(fakeReportBudget, state.reportBudget)
        assertEquals(fakeExpensesUiState, state.expensesUiState)
        assertEquals(fakeInvestmentsUiState, state.investmentsUiState)
        assertEquals(fakeSavingsUiState, state.savingsUiState)
        assertEquals(fakeBudgetsUiState, state.budgetsUiState)
        assertEquals(fakeFiRe, state.fire)
        assertFalse(state.isLoading)
        assertFalse(state.hasError)

        job.cancel()
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveFinanceUiStateUseCase.emit(Async.Error)

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

        assertEquals("coffee", fakeObserveFinanceUiStateUseCase.lastQuery)
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
        fakeObserveFinanceUiStateUseCase.emit(Async.Success(FinanceUiState()))
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
    fun `SetExpensesFiltering updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = ExpensesFilterType.DEBIT_EXPENSES

        viewModel.onIntent(FinanceIntent.SetExpensesFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.expensesFilterTypeFlow.value)
    }

    @Test
    fun `SetExpensesSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = ExpensesSortType.BY_MIN

        viewModel.onIntent(FinanceIntent.SetExpensesSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.expensesSortTypeFlow.value)
    }

    @Test
    fun `SetInvestmentsFiltering updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = "etfs"

        viewModel.onIntent(FinanceIntent.SetInvestmentsFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.investmentsFilterTypeFlow.value)
    }

    @Test
    fun `SetInvestmentsSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = InvestmentsSortType.BY_MARKET_VALUE

        viewModel.onIntent(FinanceIntent.SetInvestmentsSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.investmentsSortTypeFlow.value)
    }

    @Test
    fun `SetSavingsSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = SavingsSortType.BY_MAX

        viewModel.onIntent(FinanceIntent.SetSavingsSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.savingsSortTypeFlow.value)
    }

    @Test
    fun `SetBudgetsFiltering updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = BudgetsFilterType.POSITIVE_BUDGETS

        viewModel.onIntent(FinanceIntent.SetBudgetsFiltering(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.budgetsFilterTypeFlow.value)
    }

    @Test
    fun `SetBudgetsSorting updates finance repository`() = runTest {
        viewModel = createViewModel()

        val expected = BudgetsSortType.BY_MIN

        viewModel.onIntent(FinanceIntent.SetBudgetsSorting(expected))
        advanceUntilIdle()

        assertEquals(expected, fakeFinanceRepository.budgetsSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets search query to empty`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SearchQueryChanged("coffee"))
        advanceUntilIdle()
        assertEquals("coffee", fakeFinanceRepository.searchQueryFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals("", fakeFinanceRepository.searchQueryFlow.value)
    }

    @Test
    fun `resetFilters resets expenses filter type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetExpensesFiltering(ExpensesFilterType.DEBIT_EXPENSES))
        advanceUntilIdle()
        assertEquals(ExpensesFilterType.DEBIT_EXPENSES, fakeFinanceRepository.expensesFilterTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(ExpensesFilterType.ALL_EXPENSES, fakeFinanceRepository.expensesFilterTypeFlow.value)
    }

    @Test
    fun `resetFilters resets expenses sort type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetExpensesSorting(ExpensesSortType.BY_MIN))
        advanceUntilIdle()
        assertEquals(ExpensesSortType.BY_MIN, fakeFinanceRepository.expensesSortTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(ExpensesSortType.BY_DATE, fakeFinanceRepository.expensesSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets investments filter type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetInvestmentsFiltering("etfs"))
        advanceUntilIdle()
        assertEquals("etfs", fakeFinanceRepository.investmentsFilterTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(InvestmentsFilterType_All, fakeFinanceRepository.investmentsFilterTypeFlow.value)
    }

    @Test
    fun `resetFilters resets investments sort type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetInvestmentsSorting(InvestmentsSortType.BY_MARKET_VALUE))
        advanceUntilIdle()
        assertEquals(InvestmentsSortType.BY_MARKET_VALUE, fakeFinanceRepository.investmentsSortTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(InvestmentsSortType.BY_DATE, fakeFinanceRepository.investmentsSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets savings sort type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetSavingsSorting(SavingsSortType.BY_MAX))
        advanceUntilIdle()
        assertEquals(SavingsSortType.BY_MAX, fakeFinanceRepository.savingsSortTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(SavingsSortType.BY_DATE, fakeFinanceRepository.savingsSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets budgets filter type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetBudgetsFiltering(BudgetsFilterType.POSITIVE_BUDGETS))
        advanceUntilIdle()
        assertEquals(BudgetsFilterType.POSITIVE_BUDGETS, fakeFinanceRepository.budgetsFilterTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(BudgetsFilterType.ALL_BUDGETS, fakeFinanceRepository.budgetsFilterTypeFlow.value)
    }

    @Test
    fun `resetFilters resets budgets sort type to default`() = runTest {
        viewModel = createViewModel()

        viewModel.onIntent(FinanceIntent.SetBudgetsSorting(BudgetsSortType.BY_MIN))
        advanceUntilIdle()
        assertEquals(BudgetsSortType.BY_MIN, fakeFinanceRepository.budgetsSortTypeFlow.value)

        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        assertEquals(BudgetsSortType.BY_DATE, fakeFinanceRepository.budgetsSortTypeFlow.value)
    }

    @Test
    fun `resetFilters resets all filters and sort types at once`() = runTest {
        viewModel = createViewModel()

        // Set non-default values for everything
        viewModel.onIntent(FinanceIntent.SearchQueryChanged("coffee"))
        viewModel.onIntent(FinanceIntent.SetExpensesFiltering(ExpensesFilterType.DEBIT_EXPENSES))
        viewModel.onIntent(FinanceIntent.SetExpensesSorting(ExpensesSortType.BY_MIN))
        viewModel.onIntent(FinanceIntent.SetInvestmentsFiltering("etfs"))
        viewModel.onIntent(FinanceIntent.SetInvestmentsSorting(InvestmentsSortType.BY_MARKET_VALUE))
        viewModel.onIntent(FinanceIntent.SetSavingsSorting(SavingsSortType.BY_MAX))
        viewModel.onIntent(FinanceIntent.SetBudgetsFiltering(BudgetsFilterType.POSITIVE_BUDGETS))
        viewModel.onIntent(FinanceIntent.SetBudgetsSorting(BudgetsSortType.BY_MIN))
        advanceUntilIdle()

        // Verify non-default values are set
        assertEquals("coffee", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(ExpensesFilterType.DEBIT_EXPENSES, fakeFinanceRepository.expensesFilterTypeFlow.value)
        assertEquals(ExpensesSortType.BY_MIN, fakeFinanceRepository.expensesSortTypeFlow.value)
        assertEquals("etfs", fakeFinanceRepository.investmentsFilterTypeFlow.value)
        assertEquals(InvestmentsSortType.BY_MARKET_VALUE, fakeFinanceRepository.investmentsSortTypeFlow.value)
        assertEquals(SavingsSortType.BY_MAX, fakeFinanceRepository.savingsSortTypeFlow.value)
        assertEquals(BudgetsFilterType.POSITIVE_BUDGETS, fakeFinanceRepository.budgetsFilterTypeFlow.value)
        assertEquals(BudgetsSortType.BY_MIN, fakeFinanceRepository.budgetsSortTypeFlow.value)

        // Reset all
        viewModel.onIntent(FinanceIntent.ResetFilters)
        advanceUntilIdle()

        // Verify all reset to defaults
        assertEquals("", fakeFinanceRepository.searchQueryFlow.value)
        assertEquals(ExpensesFilterType.ALL_EXPENSES, fakeFinanceRepository.expensesFilterTypeFlow.value)
        assertEquals(ExpensesSortType.BY_DATE, fakeFinanceRepository.expensesSortTypeFlow.value)
        assertEquals(InvestmentsFilterType_All, fakeFinanceRepository.investmentsFilterTypeFlow.value)
        assertEquals(InvestmentsSortType.BY_DATE, fakeFinanceRepository.investmentsSortTypeFlow.value)
        assertEquals(SavingsSortType.BY_DATE, fakeFinanceRepository.savingsSortTypeFlow.value)
        assertEquals(BudgetsFilterType.ALL_BUDGETS, fakeFinanceRepository.budgetsFilterTypeFlow.value)
        assertEquals(BudgetsSortType.BY_DATE, fakeFinanceRepository.budgetsSortTypeFlow.value)
    }

    private fun createViewModel() = FinanceViewModel(
        fakeObserveFinanceUiStateUseCase,
        mockFireRepository,
        fakeFinanceRepository,
    )
}

