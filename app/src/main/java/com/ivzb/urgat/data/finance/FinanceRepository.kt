package com.ivzb.urgat.data.finance

import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {

    val searchQueryFlow: Flow<String>
    suspend fun updateSearchQuery(value: String)

    val expensesFilterTypeFlow: Flow<ExpensesFilterType>
    suspend fun updateExpensesFilterType(value: ExpensesFilterType)

    val expensesSortTypeFlow: Flow<ExpensesSortType>
    suspend fun updateExpensesSortType(value: ExpensesSortType)

    val investmentsFilterTypeFlow: Flow<String>
    suspend fun updateInvestmentsFilterType(value: String)

    val investmentsSortTypeFlow: Flow<InvestmentsSortType>
    suspend fun updateInvestmentsSortType(value: InvestmentsSortType)

    val savingsSortTypeFlow: Flow<SavingsSortType>
    suspend fun updateSavingsSortType(value: SavingsSortType)

    val budgetsFilterTypeFlow: Flow<BudgetsFilterType>
    suspend fun updateBudgetsFilterType(value: BudgetsFilterType)

    val budgetsSortTypeFlow: Flow<BudgetsSortType>
    suspend fun updateBudgetsSortType(value: BudgetsSortType)

    val watchlistAssetsSortTypeFlow: Flow<WatchlistAssetsSortType>
    suspend fun updateWatchlistAssetsSortType(value: WatchlistAssetsSortType)

}

