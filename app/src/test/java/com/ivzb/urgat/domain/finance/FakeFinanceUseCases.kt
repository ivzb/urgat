package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.finance.FinanceUiState
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveFinanceUiStateUseCase : ObserveFinanceUiStateUseCase {

    private val flow = MutableStateFlow<Async<FinanceUiState>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<FinanceUiState>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        expensesFilterTypeFlow: Flow<ExpensesFilterType>,
        expensesSortTypeFlow: Flow<ExpensesSortType>,
        investmentsFilterTypeFlow: Flow<String>,
        investmentsSortTypeFlow: Flow<InvestmentsSortType>,
        savingsSortTypeFlow: Flow<SavingsSortType>,
        budgetsFilterTypeFlow: Flow<BudgetsFilterType>,
        budgetsSortTypeFlow: Flow<BudgetsSortType>,
        budgetDateRangeFlow: Flow<LongRange>,
        watchlistAssetsSortTypeFlow: Flow<WatchlistAssetsSortType>,
        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,
        fireDateRangeFlow: Flow<LongRange>,
    ): Flow<Async<FinanceUiState>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

