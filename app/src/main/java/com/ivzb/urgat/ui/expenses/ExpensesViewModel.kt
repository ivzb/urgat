package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesYearlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.ALL_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.COST_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.DEBIT_EXPENSES
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    observeGroupedExpensesUseCase: ObserveGroupedExpensesUseCase,
    observeGroupedExpenseCategoriesMonthlyUseCase: ObserveGroupedExpenseCategoriesMonthlyUseCase,
    observeGroupedExpenseCategoriesYearlyUseCase: ObserveGroupedExpenseCategoriesYearlyUseCase,
    observeGroupedExpenseCategoriesAllTimeUseCase: ObserveGroupedExpenseCategoriesAllTimeUseCase,
    observeExpensesFilteringUseCase: ObserveExpensesFilteringUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType: Flow<ExpensesFilterType> = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, ALL_EXPENSES)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExpensesUiState> = combineUiState(
        observeGroupedExpensesUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeGroupedExpenseCategoriesMonthlyUseCase(_searchQuery, _savedFilterType, limitFlow = null),
        observeGroupedExpenseCategoriesYearlyUseCase(_searchQuery, _savedFilterType, limitFlow = null),
        observeGroupedExpenseCategoriesAllTimeUseCase(_searchQuery, _savedFilterType, limitFlow = null),
        observeExpensesFilteringUseCase(),

        onLoading = {
            ExpensesUiState(isLoading = true)
        },
        onError = {
            ExpensesUiState(hasError = true)
        },
        onSuccess = { expenses, categoriesMonthly, categoriesYearly, categoriesAllTime, expensesFiltering ->
            ExpensesUiState(
                expenses = expenses,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                isLoading = false,
                filteringUiInfo = getFilterUiInfo(
                    filterMenuItems = expensesFiltering,
                ),
            )
        }
    )
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