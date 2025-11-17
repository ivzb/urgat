package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.ALL_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.COST_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.DEBIT_EXPENSES
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    observeExpensesUseCase: ObserveExpensesUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, ALL_EXPENSES)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExpensesUiState> = combine(
        _filterUiInfo,
        observeExpensesUseCase(_searchQuery, _savedFilterType, NO_LIMIT)
    ) { filterUiInfo, expensesAsync ->
        when (expensesAsync) {
            Async.Loading -> {
                ExpensesUiState(isLoading = true)
            }

            is Async.Error -> {
                ExpensesUiState(hasError = true)
            }

            is Async.Success -> {
                ExpensesUiState(
                    items = expensesAsync.data,
                    filteringUiInfo = filterUiInfo,
                    isLoading = false
                )
            }
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

    private fun setFiltering(requestType: ExpensesFilterType) {
        savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] = requestType
    }

    private fun getFilterUiInfo(requestType: ExpensesFilterType): ExpensesFilteringUiInfo =
        when (requestType) {
            ALL_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.all_expenses,
                    emptyLabel = R.string.no_expenses,
                    emptyIconRes = R.drawable.logo_no_fill
                )
            }

            DEBIT_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.debit_expenses,
                    emptyLabel = R.string.no_debit_expenses,
                    emptyIconRes = R.drawable.logo_no_fill
                )
            }

            COST_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.cost_expenses,
                    emptyLabel = R.string.no_cost_expenses,
                    emptyIconRes = R.drawable.logo_no_fill
                )
            }
        }

}

const val EXPENSES_FILTER_SAVED_STATE_KEY = "EXPENSES_FILTER_SAVED_STATE_KEY"