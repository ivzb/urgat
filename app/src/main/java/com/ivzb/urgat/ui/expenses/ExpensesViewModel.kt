package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.expense.ObserveExpensesUiStateUseCase
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
    observeExpensesUiStateUseCase: ObserveExpensesUiStateUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType: Flow<ExpensesFilterType> = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, ExpensesFilterType.ALL_EXPENSES)

    private val _savedSortType: Flow<ExpensesSortType> = savedStateHandle.getStateFlow(EXPENSES_SORT_SAVED_STATE_KEY, ExpensesSortType.BY_DATE)

    private val _searchQuery = MutableStateFlow("")

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
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: ExpensesFilterType) {
        savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setSorting(sortType: ExpensesSortType) {
        savedStateHandle[EXPENSES_SORT_SAVED_STATE_KEY] = sortType
    }
}

const val EXPENSES_FILTER_SAVED_STATE_KEY = "EXPENSES_FILTER_SAVED_STATE_KEY"
const val EXPENSES_SORT_SAVED_STATE_KEY = "EXPENSES_SORT_SAVED_STATE_KEY"
