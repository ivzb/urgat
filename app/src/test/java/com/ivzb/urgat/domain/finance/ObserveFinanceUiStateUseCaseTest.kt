package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.FakeObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.FakeObserveReportBudgetUseCase
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.FakeObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.FakeObserveExpensesSortingUseCase
import com.ivzb.urgat.domain.expense.FakeObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.fire.FakeObserveFiReUseCase
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.domain.investment.FakeObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.FakeObserveInvestmentsSortingUseCase
import com.ivzb.urgat.domain.saving.FakeObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.FakeObserveSavingsSortingUseCase
import com.ivzb.urgat.domain.trades.FakeObserveGroupedTradesMonthlyUseCase
import com.ivzb.urgat.domain.trades.FakeObserveTradesFilteringUseCase
import com.ivzb.urgat.domain.trades.FakeObserveTradesSortingUseCase
import com.ivzb.urgat.domain.watchlist.FakeObserveWatchlistAssetsUseCase
import com.ivzb.urgat.domain.watchlist.FakeObserveWatchlistAssetsSortingUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.trades.TradesFilterType_All
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFinanceUiStateUseCaseTest {

    private lateinit var fakeObserveGroupedExpensesMonthlyUseCase: FakeObserveGroupedExpensesMonthlyUseCase
    private lateinit var fakeObserveExpensesFilteringUseCase: FakeObserveExpensesFilteringUseCase
    private lateinit var fakeObserveExpensesSortingUseCase: FakeObserveExpensesSortingUseCase
    private lateinit var fakeObserveGroupedInvestmentsMonthlyUseCase: FakeObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var fakeObserveInvestmentsFilteringUseCase: FakeObserveInvestmentsFilteringUseCase
    private lateinit var fakeObserveInvestmentsSortingUseCase: FakeObserveInvestmentsSortingUseCase
    private lateinit var fakeObserveGroupedTradesMonthlyUseCase: FakeObserveGroupedTradesMonthlyUseCase
    private lateinit var fakeObserveTradesFilteringUseCase: FakeObserveTradesFilteringUseCase
    private lateinit var fakeObserveTradesSortingUseCase: FakeObserveTradesSortingUseCase
    private lateinit var fakeObserveGroupedSavingsMonthlyUseCase: FakeObserveGroupedSavingsMonthlyUseCase
    private lateinit var fakeObserveSavingsSortingUseCase: FakeObserveSavingsSortingUseCase
    private lateinit var fakeObserveWatchlistAssetsUseCase: FakeObserveWatchlistAssetsUseCase
    private lateinit var fakeObserveWatchlistAssetsSortingUseCase: FakeObserveWatchlistAssetsSortingUseCase
    private lateinit var fakeObserveBudgetsUseCase: FakeObserveBudgetsUseCase
    private lateinit var fakeObserveBudgetsFilteringUseCase: FakeObserveBudgetsFilteringUseCase
    private lateinit var fakeObserveBudgetsSortingUseCase: FakeObserveBudgetsSortingUseCase
    private lateinit var fakeObserveReportBudgetUseCase: FakeObserveReportBudgetUseCase
    private lateinit var fakeObserveFiReUseCase: FakeObserveFiReUseCase

    private lateinit var useCase: ObserveFinanceUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveGroupedExpensesMonthlyUseCase = FakeObserveGroupedExpensesMonthlyUseCase()
        fakeObserveExpensesFilteringUseCase = FakeObserveExpensesFilteringUseCase()
        fakeObserveExpensesSortingUseCase = FakeObserveExpensesSortingUseCase()
        fakeObserveGroupedInvestmentsMonthlyUseCase = FakeObserveGroupedInvestmentsMonthlyUseCase()
        fakeObserveInvestmentsFilteringUseCase = FakeObserveInvestmentsFilteringUseCase()
        fakeObserveInvestmentsSortingUseCase = FakeObserveInvestmentsSortingUseCase()
        fakeObserveGroupedTradesMonthlyUseCase = FakeObserveGroupedTradesMonthlyUseCase()
        fakeObserveTradesFilteringUseCase = FakeObserveTradesFilteringUseCase()
        fakeObserveTradesSortingUseCase = FakeObserveTradesSortingUseCase()
        fakeObserveGroupedSavingsMonthlyUseCase = FakeObserveGroupedSavingsMonthlyUseCase()
        fakeObserveSavingsSortingUseCase = FakeObserveSavingsSortingUseCase()
        fakeObserveWatchlistAssetsUseCase = FakeObserveWatchlistAssetsUseCase()
        fakeObserveWatchlistAssetsSortingUseCase = FakeObserveWatchlistAssetsSortingUseCase()
        fakeObserveBudgetsUseCase = FakeObserveBudgetsUseCase()
        fakeObserveBudgetsFilteringUseCase = FakeObserveBudgetsFilteringUseCase()
        fakeObserveBudgetsSortingUseCase = FakeObserveBudgetsSortingUseCase()
        fakeObserveReportBudgetUseCase = FakeObserveReportBudgetUseCase()
        fakeObserveFiReUseCase = FakeObserveFiReUseCase()

        useCase = DefaultObserveFinanceUiStateUseCase(
            fakeObserveGroupedExpensesMonthlyUseCase,
            fakeObserveExpensesFilteringUseCase,
            fakeObserveExpensesSortingUseCase,
            fakeObserveGroupedInvestmentsMonthlyUseCase,
            fakeObserveInvestmentsFilteringUseCase,
            fakeObserveInvestmentsSortingUseCase,
            fakeObserveGroupedTradesMonthlyUseCase,
            fakeObserveTradesFilteringUseCase,
            fakeObserveTradesSortingUseCase,
            fakeObserveGroupedSavingsMonthlyUseCase,
            fakeObserveSavingsSortingUseCase,
            fakeObserveWatchlistAssetsUseCase,
            fakeObserveWatchlistAssetsSortingUseCase,
            fakeObserveBudgetsUseCase,
            fakeObserveBudgetsFilteringUseCase,
            fakeObserveBudgetsSortingUseCase,
            fakeObserveReportBudgetUseCase,
            fakeObserveFiReUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        emitAllLoading()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when any use case returns error`() = runTest {
        emitAllError()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `emits success state with all data populated`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)

        assertEquals(fakeExpenses, uiState.expensesUiState.expenses)
        assertFalse(uiState.expensesUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.expensesUiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.expensesUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeInvestments, uiState.investmentsUiState.investments)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeSavings, uiState.savingsUiState.savings)
        assertFalse(uiState.savingsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.savingsUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeBudgets, uiState.budgetsUiState.budgets)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.showSortBadge)

        assertEquals(fakeReportBudget, uiState.reportBudget)

        assertEquals(fakeFiRe, uiState.fire)
    }

    @Test
    fun `emits success state with null report budget and fire`() = runTest {
        emitAllSuccess(reportBudget = null, fire = null)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertNull(uiState.reportBudget)
        assertNull(uiState.fire)
    }

    @Test
    fun `search query is propagated to finance filtering ui info`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(searchQuery = "coffee").first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertEquals("coffee", uiState.filteringUiInfo.searchQuery)
    }

    @Test
    fun `expenses filtering ui info reflects applied filter and sort`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(
            expensesFilterType = ExpensesFilterType.DEBIT_EXPENSES,
            expensesSortType = ExpensesSortType.BY_MAX,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val expensesUiInfo = result.data.expensesUiState.filteringUiInfo
        assertTrue(expensesUiInfo.hasAppliedFilters)
        assertTrue(expensesUiInfo.showFilterBadge)
        assertTrue(expensesUiInfo.showSortBadge)
        assertEquals(R.string.debit_expenses, expensesUiInfo.currentFilteringLabel)
        assertEquals(R.string.no_expenses_search, expensesUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_search_message, expensesUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, expensesUiInfo.emptyButtonLabel)
    }

    @Test
    fun `expenses filtering ui info has no filters when defaults are used`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val expensesUiInfo = result.data.expensesUiState.filteringUiInfo
        assertFalse(expensesUiInfo.hasAppliedFilters)
        assertFalse(expensesUiInfo.showFilterBadge)
        assertFalse(expensesUiInfo.showSortBadge)
        assertEquals(R.string.transactions, expensesUiInfo.currentFilteringLabel)
        assertEquals(R.string.no_expenses, expensesUiInfo.emptyLabel)
        assertEquals(R.string.no_expenses_message, expensesUiInfo.emptyMessage)
        assertEquals(R.string.add_expense, expensesUiInfo.emptyButtonLabel)
    }

    @Test
    fun `investments filtering ui info reflects applied filter and sort`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(
            investmentsFilterType = "stocks",
            investmentsSortType = InvestmentsSortType.BY_MARKET_VALUE,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val investmentsUiInfo = result.data.investmentsUiState.filteringUiInfo
        assertTrue(investmentsUiInfo.hasAppliedFilters)
        assertTrue(investmentsUiInfo.showFilterBadge)
        assertTrue(investmentsUiInfo.showSortBadge)
        assertEquals(R.string.no_investments_search, investmentsUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_search_message, investmentsUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, investmentsUiInfo.emptyButtonLabel)
    }

    @Test
    fun `investments filtering ui info has no filters when defaults are used`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val investmentsUiInfo = result.data.investmentsUiState.filteringUiInfo
        assertFalse(investmentsUiInfo.hasAppliedFilters)
        assertFalse(investmentsUiInfo.showFilterBadge)
        assertFalse(investmentsUiInfo.showSortBadge)
        assertEquals(R.string.no_investments, investmentsUiInfo.emptyLabel)
        assertEquals(R.string.no_investments_message, investmentsUiInfo.emptyMessage)
        assertEquals(R.string.add_investment, investmentsUiInfo.emptyButtonLabel)
    }

    @Test
    fun `trades filtering ui info reflects applied filter and sort`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(
            tradesFilterType = "buy",
            tradesSortType = TradesSortType.BY_DATE,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val tradesUiInfo = result.data.tradesUiState.filteringUiInfo
        assertTrue(tradesUiInfo.hasAppliedFilters)
        assertTrue(tradesUiInfo.showFilterBadge)
        assertFalse(tradesUiInfo.showSortBadge)
        assertEquals(R.string.no_trades_search, tradesUiInfo.emptyLabel)
        assertEquals(R.string.no_trades_search_message, tradesUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, tradesUiInfo.emptyButtonLabel)
    }

    @Test
    fun `trades filtering ui info has no filters when defaults are used`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val tradesUiInfo = result.data.tradesUiState.filteringUiInfo
        assertFalse(tradesUiInfo.hasAppliedFilters)
        assertFalse(tradesUiInfo.showFilterBadge)
        assertFalse(tradesUiInfo.showSortBadge)
        assertEquals(R.string.no_trades, tradesUiInfo.emptyLabel)
        assertEquals(R.string.no_trades_message, tradesUiInfo.emptyMessage)
        assertEquals(R.string.add_trade, tradesUiInfo.emptyButtonLabel)
    }

    @Test
    fun `savings filtering ui info reflects applied sort`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(
            savingsSortType = SavingsSortType.BY_MAX,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val savingsUiInfo = result.data.savingsUiState.filteringUiInfo
        assertTrue(savingsUiInfo.hasAppliedFilters)
        assertTrue(savingsUiInfo.showSortBadge)
        assertEquals(R.string.no_savings_search, savingsUiInfo.emptyLabel)
        assertEquals(R.string.no_savings_search_message, savingsUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, savingsUiInfo.emptyButtonLabel)
    }

    @Test
    fun `savings filtering ui info has no filters when defaults are used`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val savingsUiInfo = result.data.savingsUiState.filteringUiInfo
        assertFalse(savingsUiInfo.hasAppliedFilters)
        assertFalse(savingsUiInfo.showSortBadge)
        assertEquals(R.string.no_savings, savingsUiInfo.emptyLabel)
        assertEquals(R.string.no_savings_message, savingsUiInfo.emptyMessage)
        assertEquals(R.string.add_saving, savingsUiInfo.emptyButtonLabel)
    }

    @Test
    fun `budgets filtering ui info reflects applied filter and sort`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(
            budgetsFilterType = BudgetsFilterType.POSITIVE_BUDGETS,
            budgetsSortType = BudgetsSortType.BY_MAX,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val budgetsUiInfo = result.data.budgetsUiState.filteringUiInfo
        assertTrue(budgetsUiInfo.hasAppliedFilters)
        assertTrue(budgetsUiInfo.showFilterBadge)
        assertTrue(budgetsUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets_search, budgetsUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_search_message, budgetsUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, budgetsUiInfo.emptyButtonLabel)
    }

    @Test
    fun `budgets filtering ui info has no filters when defaults are used`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val budgetsUiInfo = result.data.budgetsUiState.filteringUiInfo
        assertFalse(budgetsUiInfo.hasAppliedFilters)
        assertFalse(budgetsUiInfo.showFilterBadge)
        assertFalse(budgetsUiInfo.showSortBadge)
        assertEquals(R.string.no_budgets, budgetsUiInfo.emptyLabel)
        assertEquals(R.string.no_budgets_message, budgetsUiInfo.emptyMessage)
        assertEquals(R.string.add_budget, budgetsUiInfo.emptyButtonLabel)
    }

    @Test
    fun `search query triggers hasAppliedFilters on all sub-entities`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(searchQuery = "test").first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.expensesUiState.filteringUiInfo.hasAppliedFilters)
        assertTrue(uiState.investmentsUiState.filteringUiInfo.hasAppliedFilters)
        assertTrue(uiState.tradesUiState.filteringUiInfo.hasAppliedFilters)
        assertTrue(uiState.savingsUiState.filteringUiInfo.hasAppliedFilters)
        assertTrue(uiState.budgetsUiState.filteringUiInfo.hasAppliedFilters)
    }

    @Test
    fun `hasAppliedFilters on FinanceUiState reflects sub-entity filters`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase(
            expensesFilterType = ExpensesFilterType.COST_EXPENSES,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.expensesUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.investmentsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.savingsUiState.filteringUiInfo.hasAppliedFilters)
        assertFalse(uiState.budgetsUiState.filteringUiInfo.hasAppliedFilters)
        assertTrue(uiState.hasAppliedFilters)
    }

    @Test
    fun `hasAppliedFilters on FinanceUiState is false when no filters are applied`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertFalse(uiState.hasAppliedFilters)
    }

    @Test
    fun `expenses filter and sort menu items are populated`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val expensesUiInfo = result.data.expensesUiState.filteringUiInfo
        assertEquals(fakeExpensesFiltering, expensesUiInfo.filterMenuItems)
        assertEquals(fakeExpensesSorting, expensesUiInfo.sortMenuItems)
    }

    @Test
    fun `investments filter and sort menu items are populated`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val investmentsUiInfo = result.data.investmentsUiState.filteringUiInfo
        assertEquals(fakeInvestmentsFiltering, investmentsUiInfo.filterMenuItems)
        assertEquals(fakeInvestmentsSorting, investmentsUiInfo.sortMenuItems)
    }

    @Test
    fun `savings sort menu items are populated`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val savingsUiInfo = result.data.savingsUiState.filteringUiInfo
        assertEquals(fakeSavingsSorting, savingsUiInfo.sortMenuItems)
    }

    @Test
    fun `budgets filter and sort menu items are populated`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val budgetsUiInfo = result.data.budgetsUiState.filteringUiInfo
        assertEquals(fakeBudgetsFiltering, budgetsUiInfo.filterMenuItems)
        assertEquals(fakeBudgetsSorting, budgetsUiInfo.sortMenuItems)
    }

    @Test
    fun `pages include report when reportBudget is not null`() = runTest {
        emitAllSuccess(reportBudget = fakeReportBudget)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNotNull(result.data.reportBudget)
    }

    @Test
    fun `pages exclude report when reportBudget is null`() = runTest {
        emitAllSuccess(reportBudget = null)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNull(result.data.reportBudget)
    }

    @Test
    fun `pages include fire when fire is not null`() = runTest {
        emitAllSuccess(fire = fakeFiRe)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNotNull(result.data.fire)
    }

    @Test
    fun `pages exclude fire when fire is null`() = runTest {
        emitAllSuccess(fire = null)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertNull(result.data.fire)
    }

    @Test
    fun `investmentsUiState isRefreshing is false by default`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertFalse(result.data.investmentsUiState.isRefreshing)
    }

    @Test
    fun `investmentsUiState isRefreshing is true when watchlist is refreshing`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertTrue(result.data.investmentsUiState.isRefreshing)
    }

    @Test
    fun `investmentsUiState isRefreshing becomes false after refresh completes`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val resultRefreshing = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultRefreshing is Async.Success)
        assertTrue(resultRefreshing.data.investmentsUiState.isRefreshing)

        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(false)

        val resultDone = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultDone is Async.Success)
        assertFalse(resultDone.data.investmentsUiState.isRefreshing)
    }

    @Test
    fun `investmentsUiState isRefreshing does not affect loading state`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.investmentsUiState.isRefreshing)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `investmentsUiState isRefreshing does not affect other ui states`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedInvestmentsMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.investmentsUiState.isRefreshing)
        assertFalse(uiState.expensesUiState.isLoading)
        assertFalse(uiState.investmentsUiState.isLoading)
        assertFalse(uiState.tradesUiState.isLoading)
        assertFalse(uiState.savingsUiState.isLoading)
        assertFalse(uiState.budgetsUiState.isLoading)
    }

    @Test
    fun `tradesUiState isRefreshing is false by default`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertFalse(result.data.tradesUiState.isRefreshing)
    }

    @Test
    fun `tradesUiState isRefreshing is true when trades is refreshing`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertTrue(result.data.tradesUiState.isRefreshing)
    }

    @Test
    fun `tradesUiState isRefreshing becomes false after refresh completes`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val resultRefreshing = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultRefreshing is Async.Success)
        assertTrue(resultRefreshing.data.tradesUiState.isRefreshing)

        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(false)

        val resultDone = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultDone is Async.Success)
        assertFalse(resultDone.data.tradesUiState.isRefreshing)
    }

    @Test
    fun `tradesUiState isRefreshing does not affect loading state`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.tradesUiState.isRefreshing)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `tradesUiState isRefreshing does not affect other ui states`() = runTest {
        emitAllSuccess()
        fakeObserveGroupedTradesMonthlyUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.tradesUiState.isRefreshing)
        assertFalse(uiState.expensesUiState.isLoading)
        assertFalse(uiState.investmentsUiState.isLoading)
        assertFalse(uiState.tradesUiState.isLoading)
        assertFalse(uiState.savingsUiState.isLoading)
        assertFalse(uiState.budgetsUiState.isLoading)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing is false by default`() = runTest {
        emitAllSuccess()

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertFalse(result.data.watchlistAssetsUiState.isRefreshing)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing is true when watchlist is refreshing`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertTrue(result.data.watchlistAssetsUiState.isRefreshing)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing becomes false after refresh completes`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val resultRefreshing = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultRefreshing is Async.Success)
        assertTrue(resultRefreshing.data.watchlistAssetsUiState.isRefreshing)

        fakeObserveWatchlistAssetsUseCase.emitRefreshing(false)

        val resultDone = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(resultDone is Async.Success)
        assertFalse(resultDone.data.watchlistAssetsUiState.isRefreshing)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing does not affect loading state`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.watchlistAssetsUiState.isRefreshing)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `watchlistAssetsUiState isRefreshing does not affect other ui states`() = runTest {
        emitAllSuccess()
        fakeObserveWatchlistAssetsUseCase.emitRefreshing(true)

        val result = invokeUseCase().first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val uiState = result.data
        assertTrue(uiState.watchlistAssetsUiState.isRefreshing)
        assertFalse(uiState.expensesUiState.isLoading)
        assertFalse(uiState.investmentsUiState.isLoading)
        assertFalse(uiState.tradesUiState.isLoading)
        assertFalse(uiState.savingsUiState.isLoading)
        assertFalse(uiState.budgetsUiState.isLoading)
    }

    private fun invokeUseCase(
        searchQuery: String = "",
        expensesFilterType: ExpensesFilterType = ExpensesFilterType.ALL_EXPENSES,
        expensesSortType: ExpensesSortType = ExpensesSortType.BY_DATE,
        investmentsFilterType: String = InvestmentsFilterType_All,
        investmentsSortType: InvestmentsSortType = InvestmentsSortType.BY_DATE,
        tradesFilterType: String = TradesFilterType_All,
        tradesSortType: TradesSortType = TradesSortType.BY_DATE,
        savingsSortType: SavingsSortType = SavingsSortType.BY_DATE,
        budgetsFilterType: BudgetsFilterType = BudgetsFilterType.ALL_BUDGETS,
        budgetsSortType: BudgetsSortType = BudgetsSortType.BY_DATE,
    ) = useCase(
        searchQueryFlow = flowOf(searchQuery),
        expensesFilterTypeFlow = flowOf(expensesFilterType),
        expensesSortTypeFlow = flowOf(expensesSortType),
        investmentsFilterTypeFlow = flowOf(investmentsFilterType),
        investmentsSortTypeFlow = flowOf(investmentsSortType),
        tradesFilterTypeFlow = flowOf(tradesFilterType),
        tradesSortTypeFlow = flowOf(tradesSortType),
        savingsSortTypeFlow = flowOf(savingsSortType),
        budgetsFilterTypeFlow = flowOf(budgetsFilterType),
        budgetsSortTypeFlow = flowOf(budgetsSortType),
        budgetDateRangeFlow = flowOf(0L..Long.MAX_VALUE),
        watchlistAssetsSortTypeFlow = flowOf(WatchlistAssetsSortType.BY_DATE),
        withdrawalRateFlow = flowOf(null),
        returnOfInvestmentFlow = flowOf(null),
        taxRateFlow = flowOf(null),
        fireDateRangeFlow = flowOf(0L..Long.MAX_VALUE),
        fireDateRangeTypeFlow = flowOf(FireDateRangeType.LAST_MONTH),
        fireCustomStartFlow = flowOf(0L),
        fireCustomEndFlow = flowOf(0L),
    )

    private fun emitAllLoading() {
        fakeObserveGroupedExpensesMonthlyUseCase.emit(Async.Loading)
        fakeObserveExpensesFilteringUseCase.emit(Async.Loading)
        fakeObserveExpensesSortingUseCase.emit(Async.Loading)
        fakeObserveGroupedInvestmentsMonthlyUseCase.emit(Async.Loading)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Loading)
        fakeObserveInvestmentsSortingUseCase.emit(Async.Loading)
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Loading)
        fakeObserveTradesFilteringUseCase.emit(Async.Loading)
        fakeObserveTradesSortingUseCase.emit(Async.Loading)
        fakeObserveGroupedSavingsMonthlyUseCase.emit(Async.Loading)
        fakeObserveSavingsSortingUseCase.emit(Async.Loading)
        fakeObserveWatchlistAssetsUseCase.emit(Async.Loading)
        fakeObserveWatchlistAssetsSortingUseCase.emit(Async.Loading)
        fakeObserveBudgetsUseCase.emit(Async.Loading)
        fakeObserveBudgetsFilteringUseCase.emit(Async.Loading)
        fakeObserveBudgetsSortingUseCase.emit(Async.Loading)
        fakeObserveReportBudgetUseCase.emit(Async.Loading)
        fakeObserveFiReUseCase.emit(Async.Loading)
    }

    private fun emitAllError() {
        fakeObserveGroupedExpensesMonthlyUseCase.emit(Async.Error)
        fakeObserveExpensesFilteringUseCase.emit(Async.Error)
        fakeObserveExpensesSortingUseCase.emit(Async.Error)
        fakeObserveGroupedInvestmentsMonthlyUseCase.emit(Async.Error)
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Error)
        fakeObserveInvestmentsSortingUseCase.emit(Async.Error)
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Error)
        fakeObserveTradesFilteringUseCase.emit(Async.Error)
        fakeObserveTradesSortingUseCase.emit(Async.Error)
        fakeObserveGroupedSavingsMonthlyUseCase.emit(Async.Error)
        fakeObserveSavingsSortingUseCase.emit(Async.Error)
        fakeObserveWatchlistAssetsUseCase.emit(Async.Error)
        fakeObserveWatchlistAssetsSortingUseCase.emit(Async.Error)
        fakeObserveBudgetsUseCase.emit(Async.Error)
        fakeObserveBudgetsFilteringUseCase.emit(Async.Error)
        fakeObserveBudgetsSortingUseCase.emit(Async.Error)
        fakeObserveReportBudgetUseCase.emit(Async.Error)
        fakeObserveFiReUseCase.emit(Async.Error)
    }

    private fun emitAllSuccess(
        reportBudget: GroupedBudget? = fakeReportBudget,
        fire: FiRe? = fakeFiRe,
    ) {
        fakeObserveGroupedExpensesMonthlyUseCase.emit(Async.Success(fakeExpenses))
        fakeObserveExpensesFilteringUseCase.emit(Async.Success(fakeExpensesFiltering))
        fakeObserveExpensesSortingUseCase.emit(Async.Success(fakeExpensesSorting))
        fakeObserveGroupedInvestmentsMonthlyUseCase.emit(Async.Success(fakeInvestments))
        fakeObserveInvestmentsFilteringUseCase.emit(Async.Success(fakeInvestmentsFiltering))
        fakeObserveInvestmentsSortingUseCase.emit(Async.Success(fakeInvestmentsSorting))
        fakeObserveGroupedTradesMonthlyUseCase.emit(Async.Success(listOf()))
        fakeObserveTradesFilteringUseCase.emit(Async.Success(listOf()))
        fakeObserveTradesSortingUseCase.emit(Async.Success(listOf()))
        fakeObserveGroupedSavingsMonthlyUseCase.emit(Async.Success(fakeSavings))
        fakeObserveSavingsSortingUseCase.emit(Async.Success(fakeSavingsSorting))
        fakeObserveWatchlistAssetsUseCase.emit(Async.Success(listOf()))
        fakeObserveWatchlistAssetsSortingUseCase.emit(Async.Success(listOf()))
        fakeObserveBudgetsUseCase.emit(Async.Success(fakeBudgets))
        fakeObserveBudgetsFilteringUseCase.emit(Async.Success(fakeBudgetsFiltering))
        fakeObserveBudgetsSortingUseCase.emit(Async.Success(fakeBudgetsSorting))
        fakeObserveReportBudgetUseCase.emit(Async.Success(reportBudget))
        fakeObserveFiReUseCase.emit(Async.Success(fire))
    }

    companion object {

        private val expenses = listOf(
            Expense("1", "Lunch", BigDecimal.TEN, "EUR", "food", 1L, false, false, 1L)
        )

        private val fakeExpenses = listOf(
            GroupedItems(group = "2025/12", total = listOf(ExpenseTotal(BigDecimal.TEN, "EUR")), items = expenses)
        )

        private val fakeExpensesFiltering = listOf(
            FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 0),
            FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 1),
            FilterMenuItem(type = ExpensesFilterType.EXCLUDED_FROM_ANALYTICS, labelResId = R.string.filter_excluded_from_analytics, size = 0),
        )

        private val fakeExpensesSorting = listOf(
            SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = ExpensesSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = ExpensesSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        private val investments = listOf(
            Investment("1", "SNP", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO, "stocks", "USD", 1L, false, 1L)
        )

        private val fakeInvestments = listOf(
            GroupedItems(group = "2025/12", total = listOf(InvestmentTotal("USD", costPrice = 10.toBigDecimal(), marketPrice = 10.toBigDecimal(), pnl = 0.toBigDecimal())), items = investments)
        )

        private val fakeInvestmentsFiltering = listOf(
            FilterMenuItem(type = InvestmentsFilterType_All, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = "stocks", labelResId = R.string.filter_all, size = 1),
        )

        private val fakeInvestmentsSorting = listOf(
            SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
        )

        private val savings = listOf(
            Saving("1", "Coffee fund", BigDecimal.TEN, "EUR", 1L, false, 1L)
        )

        private val fakeSavings = listOf(
            GroupedItems(group = "2025/12", total = listOf(SavingTotal(BigDecimal.TEN, "EUR")), items = savings)
        )

        private val fakeSavingsSorting = listOf(
            SortMenuItem(type = SavingsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = SavingsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = SavingsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        private val fakeBudgets = listOf(
            Budget("1", 6500.toBigDecimal(), 8800.toBigDecimal(), 7200.toBigDecimal(), "EUR", 1L, false, 1L),
        )

        private val fakeBudgetsFiltering = listOf(
            FilterMenuItem(type = BudgetsFilterType.ALL_BUDGETS, labelResId = R.string.filter_all, size = 1),
            FilterMenuItem(type = BudgetsFilterType.POSITIVE_BUDGETS, labelResId = R.string.filter_positive, size = 1),
            FilterMenuItem(type = BudgetsFilterType.NEGATIVE_BUDGETS, labelResId = R.string.filter_negative, size = 0),
        )

        private val fakeBudgetsSorting = listOf(
            SortMenuItem(type = BudgetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = BudgetsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = BudgetsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )

        private val fakeReportBudget = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "2025/12",
                total = 6_500.toBigDecimal(),
                items = listOf(BudgetCategory(id = "1", name = "1", currency = "EUR", value = 6_500.toBigDecimal())),
            ),
            currency = "EUR",
            totalIncome = 6_500.toBigDecimal(),
            totalExpenses = 2_000.toBigDecimal(),
            totalInvestments = 3_000.toBigDecimal(),
            totalTrades = 700.toBigDecimal(),
            totalSavings = 500.toBigDecimal(),
        )

        private val fakeFiRe = FiRe(
            selection = "2025/12",
            currency = "EUR",
            monthlyIncome = 6_500.toBigDecimal(),
            monthlyExpenses = 2_000.toBigDecimal(),
            monthlyInvestments = 3_000.toBigDecimal(),
            annualIncome = 78_000.toBigDecimal(),
            annualExpenses = 24_000.toBigDecimal(),
            annualInvestments = 36_000.toBigDecimal(),
            accumulatedStash = 100_000.toBigDecimal(),
            savingsRate = 50.toBigDecimal(),
            target = 600_000.toBigDecimal(),
            withdrawalRate = 4.toBigDecimal(),
            returnOfInvestment = 7.toBigDecimal(),
            taxRate = 10.toBigDecimal(),
            yearsUntilFiRe = 10.toBigDecimal(),
        )

    }

}
