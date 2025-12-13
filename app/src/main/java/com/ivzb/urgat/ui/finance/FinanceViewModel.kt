package com.ivzb.urgat.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeInvestmentsUseCase: ObserveInvestmentsUseCase,
    resourcesProvider: ResourcesProvider,
) : ViewModel() {

    private val _uiEvent = MutableStateFlow<FinanceUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    private val _itemsLimit = MutableStateFlow(resourcesProvider.getInteger(R.integer.items_limit))

    val uiState: StateFlow<FinanceUiState> = combine(
        observeExpensesUseCase(_searchQuery, null, _itemsLimit),
        observeInvestmentsUseCase(_searchQuery, null, _itemsLimit),
        _uiEvent,
    ) { expenses, investments, uiEvent ->
        val items = listOf(expenses, investments)

        when {
            items.any { it is Async.Loading } -> {
                FinanceUiState(isLoading = true)
            }

            items.any { it is Async.Error } -> {
                FinanceUiState(hasError = true)
            }

            items.all { it is Async.Success } -> {
                FinanceUiState(
                    expenses = (expenses as Async.Success).data,
                    expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                        currentFilteringLabel = R.string.recent_expenses,
                        emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_expenses_label else R.string.no_expenses_search,
                        emptyMessage = R.string.no_expenses_message,
                        emptyButtonLabel = R.string.add_expense,
                    ),
                    investments = (investments as Async.Success).data,
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

            else -> {
                Assert.fail("finance undefined state: $items")
                FinanceUiState(hasError = true)
            }
        }
    }
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
            _uiEvent.emit(
                when (id) {
                    FinanceFabActionId.Expense -> FinanceUiEvent.AddExpense
                    FinanceFabActionId.Investment -> FinanceUiEvent.AddInvestment
                }
            )
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            _uiEvent.emit(null)
        }
    }

}