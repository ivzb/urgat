package com.ivzb.urgat.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.ObserveDashboardBudgetUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.fire.ObserveFiReUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import com.ivzb.urgat.util.currentMonthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    observeDashboardBudgetUseCase: ObserveDashboardBudgetUseCase,
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeInvestmentsUseCase: ObserveInvestmentsUseCase,
    observeSavingsUseCase: ObserveSavingsUseCase,
    observeBudgetsUseCase: ObserveBudgetsUseCase,
    observeFiReUseCase: ObserveFiReUseCase,
    resourcesProvider: ResourcesProvider,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<FinanceUiEvent>()

    private val _searchQuery = MutableStateFlow("")

    private val _budgetDateRange = MutableStateFlow(currentMonthRange())

    private val _itemsLimit = MutableStateFlow(resourcesProvider.getInteger(R.integer.items_limit))

    private val _withdrawalRate = MutableStateFlow(4.toBigDecimal())
    private val _returnOfInvestments = MutableStateFlow(7.toBigDecimal())
    private val _taxRate = MutableStateFlow(10.toBigDecimal())

    val uiState: StateFlow<FinanceUiState> = combineUiState(
        observeDashboardBudgetUseCase(_searchQuery, _budgetDateRange, null, null, _itemsLimit),
        observeExpensesUseCase(_searchQuery, null, null, null, _itemsLimit),
        observeInvestmentsUseCase(_searchQuery, null, null, null, _itemsLimit),
        observeSavingsUseCase(_searchQuery, null, null, null, _itemsLimit),
        observeBudgetsUseCase(_searchQuery, null, null, null, _itemsLimit),
        observeFiReUseCase(_withdrawalRate, _returnOfInvestments, _taxRate, _searchQuery, _budgetDateRange, null, null, _itemsLimit),
        uiEventHolder(),

        onLoading = {
            FinanceUiState(isLoading = true)
        },
        onError = {
            FinanceUiState(hasError = true)
        },
        onSuccess = { dashboardBudget, expenses, investments, savings, budgets, fire, uiEvent ->
            FinanceUiState(
                dashboardBudget = dashboardBudget,
                expenses = expenses,
                expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_expenses,
                    emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_expenses_label else R.string.no_expenses_search,
                    emptyMessage = R.string.no_expenses_message,
                    emptyButtonLabel = R.string.add_expense,
                ),
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
            is FinanceIntent.ItemsLimitChanged -> onItemsLimitChanged(intent.itemsLimit)
            is FinanceIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is FinanceIntent.FabActionClick -> onFabActionClick(intent.itemId)
            is FinanceIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onItemsLimitChanged(newLimit: Int) {
        _itemsLimit.value = newLimit
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

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

}