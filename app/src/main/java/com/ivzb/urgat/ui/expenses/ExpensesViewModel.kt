package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
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
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeExpensesFilteringUseCase: ObserveExpensesFilteringUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExpensesUiState> = combine(
        observeExpensesUseCase(_searchQuery, _savedFilterType, NO_LIMIT),
        observeExpensesFilteringUseCase(),
    ) { expensesAsync, expensesFilteringAsync ->

        val items = listOf(expensesAsync, expensesFilteringAsync)

        when {
            items.any { it is Async.Loading } -> {
                ExpensesUiState(
                    isLoading = true
                )
            }

            items.any { it is Async.Error } -> {
                ExpensesUiState(
                    hasError = true
                )
            }

            items.all { it is Async.Success } -> {
                ExpensesUiState(
                    items = (expensesAsync as Async.Success<List<Expense>>).data,
                    isLoading = false,
                    filteringUiInfo = getFilterUiInfo(
                        filterMenuItems = (expensesFilteringAsync as Async.Success<List<ExpensesFilterMenuItem>>).data
                    ),
                )
            }

            else -> error("undefined state: $items")
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
            is ExpensesIntent.SetFiltering -> setFiltering(intent.filter)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filter: ExpensesFilterMenuItem) {
        savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] = filter
    }

    private fun getFilterUiInfo(filterMenuItems: List<ExpensesFilterMenuItem>): ExpensesFilteringUiInfo {
        val filter: ExpensesFilterMenuItem? = savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY]
        val filterId = filter?.type ?: ExpensesFilterType.ALL_EXPENSES

        return when (filterId) {
            ALL_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.all_expenses,
                    emptyLabel = R.string.no_expenses,
                    emptyIconRes = R.drawable.logo_no_fill,
                    filterMenuItems = filterMenuItems,
                )
            }

            DEBIT_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.debit_expenses,
                    emptyLabel = R.string.no_debit_expenses,
                    emptyIconRes = R.drawable.logo_no_fill,
                    filterMenuItems = filterMenuItems,
                )
            }

            COST_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.cost_expenses,
                    emptyLabel = R.string.no_cost_expenses,
                    emptyIconRes = R.drawable.logo_no_fill,
                    filterMenuItems = filterMenuItems,
                )
            }
        }
    }
}

const val EXPENSES_FILTER_SAVED_STATE_KEY = "EXPENSES_FILTER_SAVED_STATE_KEY"