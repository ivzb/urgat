package com.ivzb.urgat.ui.budgets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.budget.ObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetsMonthlyUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetsYearlyUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    observeBudgetsUseCase: ObserveBudgetsUseCase,
    observeGroupedBudgetsMonthlyUseCase: ObserveGroupedBudgetsMonthlyUseCase,
    observeGroupedBudgetsYearlyUseCase: ObserveGroupedBudgetsYearlyUseCase,
    observeBudgetsFilteringUseCase: ObserveBudgetsFilteringUseCase,
    observeBudgetsSortingUseCase: ObserveBudgetsSortingUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType: Flow<BudgetsFilterType> = savedStateHandle.getStateFlow(BUDGETS_FILTER_SAVED_STATE_KEY, initialValue = BudgetsFilterType.ALL_BUDGETS)
    private val _savedSortType: Flow<BudgetsSortType> = savedStateHandle.getStateFlow(BUDGETS_SORT_SAVED_STATE_KEY, BudgetsSortType.BY_DATE)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<BudgetsUiState> = combineUiState(
        observeBudgetsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedBudgetsMonthlyUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedBudgetsYearlyUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeBudgetsFilteringUseCase(),
        observeBudgetsSortingUseCase(),

        onLoading = {
            BudgetsUiState(isLoading = true)
        },
        onError = {
            BudgetsUiState(hasError = true)
        },
        onSuccess = { budgets, monthlyBudgets, yearlyBudgets, filtering, sorting ->

            BudgetsUiState(
                budgets = budgets,
                monthlyBudgets = monthlyBudgets,
                yearlyBudgets = yearlyBudgets,
                filteringUiInfo = BudgetsFilteringUiInfo(
                    filterMenuItems = filtering,
                    sortMenuItems = sorting,
                ),
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = BudgetsUiState(isLoading = true)
    )

    fun onIntent(intent: BudgetsIntent) {
        when (intent) {
            is BudgetsIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is BudgetsIntent.SetFiltering -> setFiltering(intent.filterType)
            is BudgetsIntent.SetSorting -> setSorting(intent.sortType)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: BudgetsFilterType) {
        savedStateHandle[BUDGETS_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setSorting(sortType: BudgetsSortType) {
        savedStateHandle[BUDGETS_SORT_SAVED_STATE_KEY] = sortType
    }

}

const val BUDGETS_FILTER_SAVED_STATE_KEY = "BUDGETS_FILTER_SAVED_STATE_KEY"
const val BUDGETS_SORT_SAVED_STATE_KEY = "BUDGETS_SORT_SAVED_STATE_KEY"
