package com.ivzb.urgat.ui.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.domain.budget.ObserveBudgetsUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    observeBudgetsUiStateUseCase: ObserveBudgetsUiStateUseCase,
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    private val _savedFilterType = financeRepository.budgetsFilterTypeFlow
    private val _savedSortType = financeRepository.budgetsSortTypeFlow
    private val _searchQuery = financeRepository.searchQueryFlow

    val uiState: StateFlow<BudgetsUiState> = combineUiState(
        observeBudgetsUiStateUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType),

        onLoading = { BudgetsUiState(isLoading = true) },
        onError = { BudgetsUiState(hasError = true) },
        onSuccess = { budgetsUiState -> budgetsUiState },
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
            is BudgetsIntent.ResetFilters -> resetFilters()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            financeRepository.updateSearchQuery(newQuery)
        }
    }

    private fun setFiltering(filterType: BudgetsFilterType) {
        viewModelScope.launch {
            financeRepository.updateBudgetsFilterType(filterType)
        }
    }

    private fun setSorting(sortType: BudgetsSortType) {
        viewModelScope.launch {
            financeRepository.updateBudgetsSortType(sortType)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setFiltering(BudgetsFilterType.ALL_BUDGETS)
        setSorting(BudgetsSortType.BY_DATE)
    }

}

