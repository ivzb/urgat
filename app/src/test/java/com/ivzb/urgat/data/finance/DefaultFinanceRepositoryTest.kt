package com.ivzb.urgat.data.finance

import com.ivzb.urgat.data.dataStore.FakePreferencesDataStore
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultFinanceRepositoryTest {

    private lateinit var dataStore: FakePreferencesDataStore
    private lateinit var repository: DefaultFinanceRepository

    @Before
    fun setup() {
        dataStore = FakePreferencesDataStore()
        repository = DefaultFinanceRepository(dataStore)
    }

    @Test
    fun `searchQueryFlow emits default value when not set`() = runTest {
        assertEquals("", repository.searchQueryFlow.first())
    }

    @Test
    fun `updateSearchQuery updates flow value`() = runTest {
        repository.updateSearchQuery("coffee")
        assertEquals("coffee", repository.searchQueryFlow.first())
    }

    @Test
    fun `expensesFilterTypeFlow emits default value when not set`() = runTest {
        assertEquals(ExpensesFilterType.ALL_EXPENSES, repository.expensesFilterTypeFlow.first())
    }

    @Test
    fun `updateExpensesFilterType updates flow value`() = runTest {
        repository.updateExpensesFilterType(ExpensesFilterType.DEBIT_EXPENSES)
        assertEquals(ExpensesFilterType.DEBIT_EXPENSES, repository.expensesFilterTypeFlow.first())
    }

    @Test
    fun `expensesSortTypeFlow emits default value when not set`() = runTest {
        assertEquals(ExpensesSortType.BY_DATE, repository.expensesSortTypeFlow.first())
    }

    @Test
    fun `updateExpensesSortType updates flow value`() = runTest {
        repository.updateExpensesSortType(ExpensesSortType.BY_MIN)
        assertEquals(ExpensesSortType.BY_MIN, repository.expensesSortTypeFlow.first())
    }

    @Test
    fun `investmentsFilterTypeFlow emits default value when not set`() = runTest {
        assertEquals(InvestmentsFilterType_All, repository.investmentsFilterTypeFlow.first())
    }

    @Test
    fun `updateInvestmentsFilterType updates flow value`() = runTest {
        repository.updateInvestmentsFilterType("etfs")
        assertEquals("etfs", repository.investmentsFilterTypeFlow.first())
    }

    @Test
    fun `investmentsSortTypeFlow emits default value when not set`() = runTest {
        assertEquals(InvestmentsSortType.BY_DATE, repository.investmentsSortTypeFlow.first())
    }

    @Test
    fun `updateInvestmentsSortType updates flow value`() = runTest {
        repository.updateInvestmentsSortType(InvestmentsSortType.BY_MARKET_VALUE)
        assertEquals(InvestmentsSortType.BY_MARKET_VALUE, repository.investmentsSortTypeFlow.first())
    }

    @Test
    fun `savingsSortTypeFlow emits default value when not set`() = runTest {
        assertEquals(SavingsSortType.BY_DATE, repository.savingsSortTypeFlow.first())
    }

    @Test
    fun `updateSavingsSortType updates flow value`() = runTest {
        repository.updateSavingsSortType(SavingsSortType.BY_MAX)
        assertEquals(SavingsSortType.BY_MAX, repository.savingsSortTypeFlow.first())
    }

    @Test
    fun `budgetsFilterTypeFlow emits default value when not set`() = runTest {
        assertEquals(BudgetsFilterType.ALL_BUDGETS, repository.budgetsFilterTypeFlow.first())
    }

    @Test
    fun `updateBudgetsFilterType updates flow value`() = runTest {
        repository.updateBudgetsFilterType(BudgetsFilterType.POSITIVE_BUDGETS)
        assertEquals(BudgetsFilterType.POSITIVE_BUDGETS, repository.budgetsFilterTypeFlow.first())
    }

    @Test
    fun `budgetsSortTypeFlow emits default value when not set`() = runTest {
        assertEquals(BudgetsSortType.BY_DATE, repository.budgetsSortTypeFlow.first())
    }

    @Test
    fun `updateBudgetsSortType updates flow value`() = runTest {
        repository.updateBudgetsSortType(BudgetsSortType.BY_MIN)
        assertEquals(BudgetsSortType.BY_MIN, repository.budgetsSortTypeFlow.first())
    }

}

