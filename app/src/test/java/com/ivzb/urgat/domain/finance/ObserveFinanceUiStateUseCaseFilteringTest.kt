package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFinanceUiStateUseCaseFilteringTest : ObserveFinanceUiStateUseCaseTestBase() {

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
}
