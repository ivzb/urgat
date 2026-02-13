package com.ivzb.urgat.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.ObserveReportBudgetUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUiStateUseCase
import com.ivzb.urgat.domain.fire.ObserveFiReUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import com.ivzb.urgat.util.currentMonthRange
import com.ivzb.urgat.util.untilCurrentMonthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    observeReportBudgetUseCase: ObserveReportBudgetUseCase,
    observeExpensesUiStateUseCase: ObserveExpensesUiStateUseCase,
    observeInvestmentsUseCase: ObserveInvestmentsUseCase,
    observeSavingsUseCase: ObserveSavingsUseCase,
    observeBudgetsUseCase: ObserveBudgetsUseCase,
    observeFiReUseCase: ObserveFiReUseCase,
    private val fireRepository: FireRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<FinanceUiEvent>()

    private val _searchQuery = MutableStateFlow("")

    private val _budgetDateRange = MutableStateFlow(currentMonthRange())

    private val _withdrawalRate = fireRepository.withdrawalRateFlow

    private val _returnOfInvestment = fireRepository.returnOfInvestmentFlow

    private val _taxRate = fireRepository.taxRateFlow

    private val _fireDateRange = MutableStateFlow(untilCurrentMonthRange())

    private val _savedExpensesFilterType: Flow<ExpensesFilterType> = savedStateHandle.getStateFlow(FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY, ExpensesFilterType.ALL_EXPENSES)
    
    private val _savedExpensesSortType: Flow<ExpensesSortType> = savedStateHandle.getStateFlow(FINANCE_EXPENSES_SORT_SAVED_STATE_KEY, ExpensesSortType.BY_DATE)

    val uiState: StateFlow<FinanceUiState> = combineUiState(
        observeExpensesUiStateUseCase(_searchQuery, _savedExpensesFilterType, _savedExpensesSortType),
        observeInvestmentsUseCase(_searchQuery, null, null, null, null),
        observeSavingsUseCase(_searchQuery, null, null, null, null),
        observeBudgetsUseCase(_searchQuery, null, null, null, null),
        observeReportBudgetUseCase(_searchQuery, _budgetDateRange, null, null, null),
        observeFiReUseCase(_withdrawalRate, _returnOfInvestment, _taxRate, _searchQuery, _fireDateRange, null, null, null),
        uiEventHolder(),

        onLoading = {
            FinanceUiState(isLoading = true)
        },
        onError = {
            FinanceUiState(hasError = true)
        },
        onSuccess = { expensesUiState, investments, savings, budgets, reportBudget, fire, uiEvent ->
            FinanceUiState(
                expensesUiState = expensesUiState,
                investments = investments,
                investmentsFilteringUiInfo = InvestmentsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_investments,
                    emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_investments_label else R.string.no_investments_search,
                    emptyMessage = R.string.no_investments_message,
                    emptyButtonLabel = R.string.add_investment,
                ),
                savings = savings,
                savingsFilteringUiInfo = SavingsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_savings,
                    emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_savings_label else R.string.no_savings_search,
                    emptyMessage = R.string.no_savings_message,
                    emptyButtonLabel = R.string.add_saving,
                ),
                budgets = budgets,
                budgetsFilteringUiInfo = BudgetsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_budgets,
                    emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_budgets_label else R.string.no_budgets_search,
                    emptyMessage = R.string.no_budgets_message,
                    emptyButtonLabel = R.string.add_budget,
                ),
                reportBudget = reportBudget,
                fire = fire,
                isLoading = false,
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
            
            is FinanceIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
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
        savedStateHandle[FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setExpensesSorting(sortType: ExpensesSortType) {
        savedStateHandle[FINANCE_EXPENSES_SORT_SAVED_STATE_KEY] = sortType
    }

}

const val FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY = "FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY"
const val FINANCE_EXPENSES_SORT_SAVED_STATE_KEY = "FINANCE_EXPENSES_SORT_SAVED_STATE_KEY"