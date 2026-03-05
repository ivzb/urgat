package com.ivzb.urgat.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.domain.budget.ObserveBudgetsUiStateUseCase
import com.ivzb.urgat.domain.budget.ObserveReportBudgetUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUiStateUseCase
import com.ivzb.urgat.domain.fire.ObserveFiReUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUiStateUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsUiStateUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import com.ivzb.urgat.util.currentMonthRange
import com.ivzb.urgat.util.untilCurrentMonthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    observeExpensesUiStateUseCase: ObserveExpensesUiStateUseCase,
    observeInvestmentsUiStateUseCase: ObserveInvestmentsUiStateUseCase,
    observeSavingsUiStateUseCase: ObserveSavingsUiStateUseCase,
    observeBudgetsUiStateUseCase: ObserveBudgetsUiStateUseCase,
    observeReportBudgetUseCase: ObserveReportBudgetUseCase,
    observeFiReUseCase: ObserveFiReUseCase,
    private val fireRepository: FireRepository,
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<FinanceUiEvent>()

    private val _searchQuery = financeRepository.searchQueryFlow

    private val _budgetDateRange = MutableStateFlow(currentMonthRange())

    private val _withdrawalRate = fireRepository.withdrawalRateFlow

    private val _returnOfInvestment = fireRepository.returnOfInvestmentFlow

    private val _taxRate = fireRepository.taxRateFlow

    private val _fireDateRange = MutableStateFlow(untilCurrentMonthRange())

    private val _savedExpensesFilterType = financeRepository.expensesFilterTypeFlow
    private val _savedExpensesSortType = financeRepository.expensesSortTypeFlow

    private val _savedInvestmentsFilterType = financeRepository.investmentsFilterTypeFlow
    private val _savedInvestmentsSortType = financeRepository.investmentsSortTypeFlow

    private val _savedSavingsSortType = financeRepository.savingsSortTypeFlow

    private val _savedBudgetsFilterType = financeRepository.budgetsFilterTypeFlow
    private val _savedBudgetsSortType = financeRepository.budgetsSortTypeFlow

    val uiState: StateFlow<FinanceUiState> = combineUiState(
        observeExpensesUiStateUseCase(_searchQuery, _savedExpensesFilterType, _savedExpensesSortType),
        observeInvestmentsUiStateUseCase(_searchQuery, _savedInvestmentsFilterType, _savedInvestmentsSortType),
        observeSavingsUiStateUseCase(_searchQuery, _savedSavingsSortType),
        observeBudgetsUiStateUseCase(_searchQuery, _savedBudgetsFilterType, _savedBudgetsSortType),
        observeReportBudgetUseCase(_budgetDateRange),
        observeFiReUseCase(_withdrawalRate, _returnOfInvestment, _taxRate, _fireDateRange),
        _searchQuery.map { Async.Success(it) },
        uiEventHolder(),

        onLoading = {
            FinanceUiState(isLoading = true)
        },
        onError = {
            FinanceUiState(hasError = true)
        },
        onSuccess = { expensesUiState, investmentsUiState, savingsUiState, budgetsUiState, reportBudget, fire, searchQuery, uiEvent ->
            FinanceUiState(
                expensesUiState = expensesUiState,
                investmentsUiState = investmentsUiState,
                savingsUiState = savingsUiState,
                budgetsUiState = budgetsUiState,
                reportBudget = reportBudget,
                fire = fire,
                isLoading = false,
                filteringUiInfo = FinanceFilteringUiInfo(
                    searchQuery = searchQuery,
                ),
                uiEvent = uiEvent,
            )
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
            is FinanceIntent.FabActionClick -> onFabActionClick(intent.itemId)
            is FinanceIntent.UpdateFireWithdrawalRate -> onUpdateFireWithdrawalRate(intent.withdrawalRate)
            is FinanceIntent.UpdateFireReturnOfInvestment -> onUpdateFireReturnOfInvestment(intent.returnOfInvestment)
            is FinanceIntent.UpdateFireTaxRate -> onUpdateFireTaxRate(intent.taxRate)

            is FinanceIntent.SetExpensesFiltering -> setExpensesFiltering(intent.filterType)
            is FinanceIntent.SetExpensesSorting -> setExpensesSorting(intent.sortType)

            is FinanceIntent.SetInvestmentsFiltering -> setInvestmentsFiltering(intent.filterType)
            is FinanceIntent.SetInvestmentsSorting -> setInvestmentsSorting(intent.sortType)

            is FinanceIntent.SetSavingsSorting -> setSavingsSorting(intent.sortType)

            is FinanceIntent.SetBudgetsFiltering -> setBudgetsFiltering(intent.filterType)
            is FinanceIntent.SetBudgetsSorting -> setBudgetsSorting(intent.sortType)

            is FinanceIntent.ResetFilters -> resetFilters()

            is FinanceIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            financeRepository.updateSearchQuery(newQuery)
        }
    }

    private fun onFabActionClick(id: FinanceFabActionId) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (id) {
                    FinanceFabActionId.Expense -> FinanceUiEvent.AddExpense
                    FinanceFabActionId.Investment -> FinanceUiEvent.AddInvestment
                    FinanceFabActionId.Saving -> FinanceUiEvent.AddSaving
                    FinanceFabActionId.Budget -> FinanceUiEvent.AddBudget
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

    private fun resetFilters() {
        onSearchQueryChanged("")
        setExpensesFiltering(ExpensesFilterType.ALL_EXPENSES)
        setExpensesSorting(ExpensesSortType.BY_DATE)
        setInvestmentsFiltering(InvestmentsFilterType_All)
        setInvestmentsSorting(InvestmentsSortType.BY_DATE)
        setBudgetsFiltering(BudgetsFilterType.ALL_BUDGETS)
        setBudgetsSorting(BudgetsSortType.BY_DATE)
        setSavingsSorting(SavingsSortType.BY_DATE)
    }

}