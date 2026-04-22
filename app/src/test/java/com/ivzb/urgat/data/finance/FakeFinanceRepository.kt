package com.ivzb.urgat.data.finance

import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.trades.TradesFilterType_All
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFinanceRepository : FinanceRepository {

    override val searchQueryFlow = MutableStateFlow("")
    override suspend fun updateSearchQuery(value: String) { searchQueryFlow.value = value }

    override val expensesFilterTypeFlow = MutableStateFlow(ExpensesFilterType.ALL_EXPENSES)
    override suspend fun updateExpensesFilterType(value: ExpensesFilterType) { expensesFilterTypeFlow.value = value }

    override val expensesSortTypeFlow = MutableStateFlow(ExpensesSortType.BY_DATE)
    override suspend fun updateExpensesSortType(value: ExpensesSortType) { expensesSortTypeFlow.value = value }

    override val investmentsFilterTypeFlow = MutableStateFlow(InvestmentsFilterType_All)
    override suspend fun updateInvestmentsFilterType(value: String) { investmentsFilterTypeFlow.value = value }

    override val investmentsSortTypeFlow = MutableStateFlow(InvestmentsSortType.BY_DATE)
    override suspend fun updateInvestmentsSortType(value: InvestmentsSortType) { investmentsSortTypeFlow.value = value }

    override val tradesFilterTypeFlow = MutableStateFlow(TradesFilterType_All)
    override suspend fun updateTradesFilterType(value: String) { tradesFilterTypeFlow.value = value }

    override val tradesSortTypeFlow = MutableStateFlow(TradesSortType.BY_DATE)
    override suspend fun updateTradesSortType(value: TradesSortType) { tradesSortTypeFlow.value = value }

    override val savingsSortTypeFlow = MutableStateFlow(SavingsSortType.BY_DATE)
    override suspend fun updateSavingsSortType(value: SavingsSortType) { savingsSortTypeFlow.value = value }

    override val budgetsFilterTypeFlow = MutableStateFlow(BudgetsFilterType.ALL_BUDGETS)
    override suspend fun updateBudgetsFilterType(value: BudgetsFilterType) { budgetsFilterTypeFlow.value = value }

    override val budgetsSortTypeFlow = MutableStateFlow(BudgetsSortType.BY_DATE)
    override suspend fun updateBudgetsSortType(value: BudgetsSortType) { budgetsSortTypeFlow.value = value }

    override val watchlistAssetsSortTypeFlow = MutableStateFlow(WatchlistAssetsSortType.BY_DATE)
    override suspend fun updateWatchlistAssetsSortType(value: WatchlistAssetsSortType) { watchlistAssetsSortTypeFlow.value = value }

}