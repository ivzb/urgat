package com.ivzb.urgat.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.domain.finance.ObserveFinanceUiStateUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.ui.investments.INVESTMENTS_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.trades.TRADES_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import com.ivzb.urgat.util.currentMonthRange
import com.ivzb.urgat.util.lastMonthRange
import com.ivzb.urgat.util.lastYearRange
import com.ivzb.urgat.util.yearToDateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
@Suppress("TooManyFunctions")
class FinanceViewModel @Inject constructor(
    observeFinanceUiStateUseCase: ObserveFinanceUiStateUseCase,
    private val fireRepository: FireRepository,
    private val financeRepository: FinanceRepository,
    private val forexRepository: ForexRepository,
    private val marketPriceRepository: MarketPriceRepository,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<FinanceUiEvent>()

    private val searchQueryFlow = financeRepository.searchQueryFlow

    private val budgetDateRangeFlow = MutableStateFlow(currentMonthRange())

    private val withdrawalRateFlow = fireRepository.withdrawalRateFlow

    private val returnOfInvestmentFlow = fireRepository.returnOfInvestmentFlow

    private val taxRateFlow = fireRepository.taxRateFlow

    private val fireDateRangeTypeFlow = fireRepository.fireDateRangeTypeFlow

    private val fireCustomStartFlow = fireRepository.fireCustomStartFlow

    private val fireCustomEndFlow = fireRepository.fireCustomEndFlow

    private val fireDateRangeFlow = combine(
        fireDateRangeTypeFlow,
        fireCustomStartFlow,
        fireCustomEndFlow,
    ) { type, customStart, customEnd ->
        when (type) {
            FireDateRangeType.LAST_MONTH -> lastMonthRange()

            FireDateRangeType.LAST_YEAR -> lastYearRange()

            FireDateRangeType.YTD -> yearToDateRange()

            FireDateRangeType.CUSTOM -> if (customStart > 0 && customEnd > 0) {
                customStart .. customEnd
            } else {
                lastMonthRange()
            }
        }
    }

    private val savedExpensesFilterTypeFlow = financeRepository.expensesFilterTypeFlow
    private val savedExpensesSortTypeFlow = financeRepository.expensesSortTypeFlow

    private val savedInvestmentsFilterTypeFlow = financeRepository.investmentsFilterTypeFlow
    private val savedInvestmentsSortTypeFlow = financeRepository.investmentsSortTypeFlow

    private val savedTradesFilterTypeFlow = financeRepository.tradesFilterTypeFlow
    private val savedTradesSortTypeFlow = financeRepository.tradesSortTypeFlow

    private val savedSavingsSortTypeFlow = financeRepository.savingsSortTypeFlow

    private val savedBudgetsFilterTypeFlow = financeRepository.budgetsFilterTypeFlow
    private val savedBudgetsSortTypeFlow = financeRepository.budgetsSortTypeFlow

    private val savedWatchlistAssetsSortTypeFlow = financeRepository.watchlistAssetsSortTypeFlow

    val uiState: StateFlow<FinanceUiState> = combineUiState(
        observeFinanceUiStateUseCase(
            searchQueryFlow = searchQueryFlow,
            expensesFilterTypeFlow = savedExpensesFilterTypeFlow,
            expensesSortTypeFlow = savedExpensesSortTypeFlow,
            investmentsFilterTypeFlow = savedInvestmentsFilterTypeFlow,
            investmentsSortTypeFlow = savedInvestmentsSortTypeFlow,
            tradesFilterTypeFlow = savedTradesFilterTypeFlow,
            tradesSortTypeFlow = savedTradesSortTypeFlow,
            savingsSortTypeFlow = savedSavingsSortTypeFlow,
            budgetsFilterTypeFlow = savedBudgetsFilterTypeFlow,
            budgetsSortTypeFlow = savedBudgetsSortTypeFlow,
            watchlistAssetsSortTypeFlow = savedWatchlistAssetsSortTypeFlow,
            budgetDateRangeFlow = budgetDateRangeFlow,
            withdrawalRateFlow = withdrawalRateFlow,
            returnOfInvestmentFlow = returnOfInvestmentFlow,
            taxRateFlow = taxRateFlow,
            fireDateRangeFlow = fireDateRangeFlow,
            fireDateRangeTypeFlow = fireDateRangeTypeFlow,
            fireCustomStartFlow = fireCustomStartFlow,
            fireCustomEndFlow = fireCustomEndFlow,
        ),
        uiEventHolder(),

        onLoading = {
            FinanceUiState(isLoading = true)
        },
        onError = {
            FinanceUiState(hasError = true)
        },
        onSuccess = { financeUiState, uiEvent ->
            financeUiState.copy(uiEvent = uiEvent)
        }
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = FinanceUiState(isLoading = true)
        )

    fun onIntent(intent: FinanceIntent) {
        when (intent) {
            is FinanceIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)

            is FinanceIntent.FabActionClick -> {
                onFabActionClick(intent.page)
            }

            is FinanceIntent.UpdateFireWithdrawalRate -> onUpdateFireWithdrawalRate(intent.withdrawalRate)

            is FinanceIntent.UpdateFireReturnOfInvestment -> onUpdateFireReturnOfInvestment(intent.returnOfInvestment)

            is FinanceIntent.UpdateFireTaxRate -> onUpdateFireTaxRate(intent.taxRate)

            is FinanceIntent.UpdateFireDateRangeType -> onUpdateFireDateRangeType(intent.type)

            is FinanceIntent.UpdateFireCustomDateRange -> onUpdateFireCustomDateRange(intent.start, intent.end)

            is FinanceIntent.SetExpensesFiltering -> setExpensesFiltering(intent.filterType)

            is FinanceIntent.SetExpensesSorting -> setExpensesSorting(intent.sortType)

            is FinanceIntent.SetInvestmentsFiltering -> setInvestmentsFiltering(intent.filterType)

            is FinanceIntent.SetInvestmentsSorting -> setInvestmentsSorting(intent.sortType)

            is FinanceIntent.SetTradesFiltering -> setTradesFiltering(intent.filterType)

            is FinanceIntent.SetTradesSorting -> setTradesSorting(intent.sortType)

            is FinanceIntent.SetSavingsSorting -> setSavingsSorting(intent.sortType)

            is FinanceIntent.SetBudgetsFiltering -> setBudgetsFiltering(intent.filterType)

            is FinanceIntent.SetBudgetsSorting -> setBudgetsSorting(intent.sortType)

            is FinanceIntent.SetWatchlistAssetsSorting -> setWatchlistAssetsSorting(intent.sortType)

            is FinanceIntent.RefreshInvestments -> refreshInvestments()

            is FinanceIntent.RefreshTrades -> refreshTrades()

            is FinanceIntent.RefreshWatchlist -> refreshWatchlist()

            is FinanceIntent.ResetFilters -> resetFilters()

            is FinanceIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            financeRepository.updateSearchQuery(newQuery)
        }
    }

    private fun onFabActionClick(page: Page) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (page) {
                    is FinancePage.Expenses -> FinanceUiEvent.AddExpense
                    is FinancePage.Investments -> FinanceUiEvent.AddInvestment
                    is FinancePage.Trades -> FinanceUiEvent.AddTrade
                    is FinancePage.Savings -> FinanceUiEvent.AddSaving
                    is FinancePage.Budgets -> FinanceUiEvent.AddBudget
                    is FinancePage.Watchlist -> FinanceUiEvent.AddWatchlistAsset
                    else -> null
                }
            )
        }
    }

    private fun onUpdateFireWithdrawalRate(withdrawalRate: String) {
        viewModelScope.launch {
            fireRepository.updateWithdrawalRate(withdrawalRate)
        }
    }

    private fun onUpdateFireReturnOfInvestment(returnOfInvestment: String) {
        viewModelScope.launch {
            fireRepository.updateReturnOfInvestment(returnOfInvestment)
        }
    }

    private fun onUpdateFireTaxRate(taxRate: String) {
        viewModelScope.launch {
            fireRepository.updateTaxRate(taxRate)
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

    private fun setExpensesFiltering(filterType: ExpensesFilterType) {
        viewModelScope.launch {
            financeRepository.updateExpensesFilterType(filterType)
        }
    }

    private fun setExpensesSorting(sortType: ExpensesSortType) {
        viewModelScope.launch {
            financeRepository.updateExpensesSortType(sortType)
        }
    }

    private fun setInvestmentsFiltering(filterType: String) {
        viewModelScope.launch {
            financeRepository.updateInvestmentsFilterType(filterType)
        }
    }

    private fun setInvestmentsSorting(sortType: InvestmentsSortType) {
        viewModelScope.launch {
            financeRepository.updateInvestmentsSortType(sortType)
        }
    }

    private fun setTradesFiltering(filterType: String) {
        viewModelScope.launch {
            financeRepository.updateTradesFilterType(filterType)
        }
    }

    private fun setTradesSorting(sortType: TradesSortType) {
        viewModelScope.launch {
            financeRepository.updateTradesSortType(sortType)
        }
    }

    private fun setSavingsSorting(sortType: SavingsSortType) {
        viewModelScope.launch {
            financeRepository.updateSavingsSortType(sortType)
        }
    }

    private fun setBudgetsFiltering(filterType: BudgetsFilterType) {
        viewModelScope.launch {
            financeRepository.updateBudgetsFilterType(filterType)
        }
    }

    private fun setBudgetsSorting(sortType: BudgetsSortType) {
        viewModelScope.launch {
            financeRepository.updateBudgetsSortType(sortType)
        }
    }

    private fun setWatchlistAssetsSorting(sortType: WatchlistAssetsSortType) {
        viewModelScope.launch {
            financeRepository.updateWatchlistAssetsSortType(sortType)
        }
    }

    private fun onUpdateFireDateRangeType(type: FireDateRangeType) {
        viewModelScope.launch {
            fireRepository.updateFireDateRangeType(type)
        }
    }

    private fun onUpdateFireCustomDateRange(start: Long, end: Long) {
        viewModelScope.launch {
            fireRepository.updateFireCustomStart(start)
            fireRepository.updateFireCustomEnd(end)
            fireRepository.updateFireDateRangeType(FireDateRangeType.CUSTOM)
        }
    }

    private fun refreshInvestments() {
        viewModelScope.launch {
            forexRepository.clearCache()
            marketPriceRepository.clearCache()
        }
    }

    private fun refreshTrades() {
        viewModelScope.launch {
            forexRepository.clearCache()
            marketPriceRepository.clearCache()
        }
    }

    private fun refreshWatchlist() {
        viewModelScope.launch {
            marketPriceRepository.clearCache()
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setExpensesFiltering(ExpensesFilterType.ALL_EXPENSES)
        setExpensesSorting(ExpensesSortType.BY_DATE)
        setInvestmentsFiltering(INVESTMENTS_FILTER_TYPE_ALL)
        setInvestmentsSorting(InvestmentsSortType.BY_DATE)
        setTradesFiltering(TRADES_FILTER_TYPE_ALL)
        setTradesSorting(TradesSortType.BY_DATE)
        setBudgetsFiltering(BudgetsFilterType.ALL_BUDGETS)
        setBudgetsSorting(BudgetsSortType.BY_DATE)
        setSavingsSorting(SavingsSortType.BY_DATE)
        setWatchlistAssetsSorting(WatchlistAssetsSortType.BY_DATE)
    }

}
