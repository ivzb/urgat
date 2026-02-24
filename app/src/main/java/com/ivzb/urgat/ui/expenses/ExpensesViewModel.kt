package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.domain.expense.ObserveExpensesUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    observeExpensesUiStateUseCase: ObserveExpensesUiStateUseCase,
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    private val _savedFilterType = financeRepository.expensesFilterTypeFlow
    private val _savedSortType = financeRepository.expensesSortTypeFlow
    private val _searchQuery = financeRepository.searchQueryFlow

    val uiState: StateFlow<ExpensesUiState> = combineUiState(
        observeExpensesUiStateUseCase(_searchQuery, _savedFilterType, _savedSortType),

        onLoading = { ExpensesUiState(isLoading = true) },
        onError = { ExpensesUiState(hasError = true) },
        onSuccess = { expensesUiState -> expensesUiState },
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
        viewModelScope.launch { financeRepository.updateSearchQuery(newQuery) }
    }

    private fun setFiltering(filterType: ExpensesFilterType) {
        viewModelScope.launch { financeRepository.updateExpensesFilterType(filterType) }
    }

    private fun setSorting(sortType: ExpensesSortType) {
        viewModelScope.launch { financeRepository.updateExpensesSortType(sortType) }
    }
}

