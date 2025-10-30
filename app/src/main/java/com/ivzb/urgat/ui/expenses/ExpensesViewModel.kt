package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.useCase.ObserveExpensesUseCase
import com.ivzb.urgat.ui.components.FilteringUiInfo
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
import java.math.BigDecimal
import javax.inject.Inject

data class ExpensesUiState(
    val items: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo()
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    observeExpensesUseCase: ObserveExpensesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(EXPENSES_FILTER_SAVED_STATE_KEY, ALL_EXPENSES)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()

    private val _isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ExpensesUiState> = combine(
        _filterUiInfo,
        _isLoading,
        observeExpensesUseCase(_searchQuery, _savedFilterType)
    ) { filterUiInfo, isLoading, expensesAsync ->
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
                    isLoading = isLoading
                )
            }
        }
     }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = ExpensesUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun setFiltering(requestType: ExpensesFilterType) {
        savedStateHandle[EXPENSES_FILTER_SAVED_STATE_KEY] = requestType
    }

    private fun filterExpenses(expenses: List<Expense>, filteringType: ExpensesFilterType): List<Expense> {
        val expensesToShow = ArrayList<Expense>()

        for (expense in expenses) {
            when (filteringType) {
                ALL_EXPENSES -> expensesToShow.add(expense)

                COST_EXPENSES -> if (expense.amount > BigDecimal.ZERO) {
                    expensesToShow.add(expense)
                }

                DEBIT_EXPENSES -> if (expense.amount < BigDecimal.ZERO) {
                    expensesToShow.add(expense)
                }
            }
        }
        return expensesToShow
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

data class ExpensesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_expenses,

    override val emptyLabel: Int = R.string.no_expenses,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_expenses,

    override val errorLabel: Int = R.string.error_expenses,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
