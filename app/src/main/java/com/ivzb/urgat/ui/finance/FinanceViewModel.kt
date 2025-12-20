package com.ivzb.urgat.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase
import com.ivzb.urgat.ui.common.UiEventHolder
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeInvestmentsUseCase: ObserveInvestmentsUseCase,
    resourcesProvider: ResourcesProvider,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<FinanceUiEvent>()

    private val _searchQuery = MutableStateFlow("")

    private val _itemsLimit = MutableStateFlow(resourcesProvider.getInteger(R.integer.items_limit))

    val uiState: StateFlow<FinanceUiState> = combineUiState(
        observeExpensesUseCase(_searchQuery, null, _itemsLimit),
        observeInvestmentsUseCase(_searchQuery, null, _itemsLimit),
        uiEventHolder(),

        onLoading = {
            FinanceUiState(isLoading = true)
        },
        onError = {
            FinanceUiState(hasError = true)
        },
        onSuccess = { expenses, investments, uiEvent ->
            FinanceUiState(
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