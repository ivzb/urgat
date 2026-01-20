package com.ivzb.urgat.ui.budgets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.budget.ObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    observeBudgetsUseCase: ObserveBudgetsUseCase,
    observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    observeGroupedExpensesUseCase: ObserveGroupedExpensesUseCase,
    observeGroupedInvestmentsUseCase: ObserveGroupedInvestmentsUseCase,
    observeBudgetsFilteringUseCase: ObserveBudgetsFilteringUseCase,
    observeBudgetsSortingUseCase: ObserveBudgetsSortingUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType: Flow<BudgetsFilterType> = savedStateHandle.getStateFlow(BUDGETS_FILTER_SAVED_STATE_KEY, initialValue = BudgetsFilterType.ALL_BUDGETS)
    private val _savedSortType: Flow<BudgetsSortType> = savedStateHandle.getStateFlow(BUDGETS_SORT_SAVED_STATE_KEY, BudgetsSortType.BY_DATE)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<BudgetsUiState> = combineUiState(
        observeBudgetsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, sortTypeFlow = _savedSortType, limitFlow = null),
        observeGroupedExpensesUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedInvestmentsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeBudgetsFilteringUseCase(),
        observeBudgetsSortingUseCase(),

        onLoading = {
            BudgetsUiState(isLoading = true)
        },
        onError = {
            BudgetsUiState(hasError = true)
        },
        onSuccess = { budgets, budgetCategoriesMonthly, expensesMonthly, investmentsMonthly, filtering, sorting ->
            val monthlyBudgets = budgetCategoriesMonthly.mapIndexed { index, category ->
                val expenses = expensesMonthly.getOrNull(index)
                val investments = investmentsMonthly.getOrNull(index)

                MonthlyBudgetUiState(
                    category = category,
                    totalIncome = category.total,
                    totalExpenses = expenses?.total ?: BigDecimal.ZERO,
                    totalInvestments = investments?.items?.sumOf { it.marketValue } ?: BigDecimal.ZERO,
                    totalInvestmentsFormatted = investments?.total ?: "",
                )
            }

            BudgetsUiState(
                budgets = budgets,
                monthlyBudgets = monthlyBudgets,
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
