package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.ALL_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.COST_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.DEBIT_EXPENSES
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    observeGroupedExpensesUseCase: ObserveGroupedExpensesUseCase,
    observeExpensesFilteringUseCase: ObserveExpensesFilteringUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType: Flow<ExpensesFilterType> = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, ALL_EXPENSES)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExpensesUiState> = combine(
        observeGroupedExpensesUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeExpensesFilteringUseCase(),
    ) { expensesAsync, expensesFilteringAsync ->

        when {
            expensesAsync is Async.Loading || expensesFilteringAsync is Async.Loading -> {
                ExpensesUiState(
                    isLoading = true
                )
            }

            expensesAsync is Async.Error || expensesFilteringAsync is Async.Error -> {
                ExpensesUiState(
                    hasError = true
                )
            }

            expensesAsync is Async.Success && expensesFilteringAsync is Async.Success -> {
                ExpensesUiState(
                    items = expensesAsync.data,
                    isLoading = false,
                    filteringUiInfo = getFilterUiInfo(
                        filterMenuItems = expensesFilteringAsync.data,
                    ),
                )
            }

            else -> error("undefined state: $expensesAsync, $expensesFilteringAsync")
        }
     }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ExpensesUiState(isLoading = true)
    )

    fun onIntent(intent: ExpensesIntent) {
        when (intent) {
            is ExpensesIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is ExpensesIntent.SetFiltering -> setFiltering(intent.filterType)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: ExpensesFilterType) {
        savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun getFilterUiInfo(filterMenuItems: List<ExpensesFilterMenuItem>): ExpensesFilteringUiInfo {
        val filterType: ExpensesFilterType = savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] ?: ALL_EXPENSES

        return when (filterType) {
            ALL_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.all_expenses,
                    emptyLabel = R.string.no_expenses,
                    filterMenuItems = filterMenuItems,
                )
            }

            DEBIT_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.debit_expenses,
                    emptyLabel = R.string.no_debit_expenses,
                    filterMenuItems = filterMenuItems,
                )
            }

            COST_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.cost_expenses,
                    emptyLabel = R.string.no_cost_expenses,
                    filterMenuItems = filterMenuItems,
                )
            }
        }
    }
}

const val EXPENSES_FILTER_SAVED_STATE_KEY = "EXPENSES_FILTER_SAVED_STATE_KEY"