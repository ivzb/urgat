package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesSortingUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesYearlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.*
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
    observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    observeGroupedExpenseCategoriesMonthlyUseCase: ObserveGroupedExpenseCategoriesMonthlyUseCase,
    observeGroupedExpenseCategoriesYearlyUseCase: ObserveGroupedExpenseCategoriesYearlyUseCase,
    observeGroupedExpenseCategoriesAllTimeUseCase: ObserveGroupedExpenseCategoriesAllTimeUseCase,
    observeExpensesFilteringUseCase: ObserveExpensesFilteringUseCase,
    observeExpensesSortingUseCase: ObserveExpensesSortingUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType: Flow<ExpensesFilterType> = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, ExpensesFilterType.ALL_EXPENSES)
    private val _savedSortType: Flow<ExpensesSortType> = savedStateHandle.getStateFlow(EXPENSES_SORT_SAVED_STATE_KEY, ExpensesSortType.BY_DATE)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExpensesUiState> = combineUiState(
        observeGroupedExpensesMonthlyUseCase(searchQueryFlow = _searchQuery, dateRangeFlow = null, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedExpenseCategoriesMonthlyUseCase(searchQueryFlow = _searchQuery, dateRangeFlow = null, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedExpenseCategoriesYearlyUseCase(searchQueryFlow = _searchQuery, dateRangeFlow = null, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedExpenseCategoriesAllTimeUseCase(searchQueryFlow = _searchQuery, dateRangeFlow = null, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeExpensesFilteringUseCase(),
        observeExpensesSortingUseCase(),

        onLoading = {
            ExpensesUiState(isLoading = true)
        },
        onError = {
            ExpensesUiState(hasError = true)
        },
        onSuccess = { expenses, categoriesMonthly, categoriesYearly, categoriesAllTime, filtering, sorting ->
            ExpensesUiState(
                expenses = expenses,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                isLoading = false,
                filteringUiInfo = getFilterUiInfo(
                    filterMenuItems = filtering,
                    sortMenuItems = sorting,
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
            is ExpensesIntent.SetSorting -> setSorting(intent.sortType)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: ExpensesFilterType) {
        savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setSorting(sortType: ExpensesSortType) {
        savedStateHandle[EXPENSES_SORT_SAVED_STATE_KEY] = sortType
    }

    private fun getFilterUiInfo(
        filterMenuItems: List<ExpensesFilterMenuItem>,
        sortMenuItems: List<ExpensesSortMenuItem>,
    ): ExpensesFilteringUiInfo {
        val filterType: ExpensesFilterType = savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] ?: ALL_EXPENSES

        return when (filterType) {
            ALL_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.all_expenses,
                    emptyLabel = R.string.no_expenses,
                    filterMenuItems = filterMenuItems,
                    sortMenuItems = sortMenuItems,
                )
            }

            DEBIT_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.debit_expenses,
                    emptyLabel = R.string.no_debit_expenses,
                    filterMenuItems = filterMenuItems,
                    sortMenuItems = sortMenuItems,
                )
            }

            COST_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.cost_expenses,
                    emptyLabel = R.string.no_cost_expenses,
                    filterMenuItems = filterMenuItems,
                    sortMenuItems = sortMenuItems,
                )
            }

            EXCLUDED_FROM_ANALYTICS -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.exclude_from_analytics,
                    emptyLabel = R.string.no_excluded_from_analytics_expenses,
                    filterMenuItems = filterMenuItems,
                    sortMenuItems = sortMenuItems,
                )
            }
        }
    }
}

const val EXPENSES_FILTER_SAVED_STATE_KEY = "EXPENSES_FILTER_SAVED_STATE_KEY"
const val EXPENSES_SORT_SAVED_STATE_KEY = "EXPENSES_SORT_SAVED_STATE_KEY"
