package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.expenses.ExpensesFilterMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.ALL_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.COST_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.DEBIT_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.EXCLUDED_FROM_ANALYTICS
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesSortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveExpensesUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>,
        sortTypeFlow: Flow<ExpensesSortType>,
    ): Flow<Async<ExpensesUiState>>

}

class DefaultObserveExpensesUiStateUseCase @Inject constructor(
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedExpenseCategoriesMonthlyUseCase: ObserveGroupedExpenseCategoriesMonthlyUseCase,
    private val observeGroupedExpenseCategoriesYearlyUseCase: ObserveGroupedExpenseCategoriesYearlyUseCase,
    private val observeGroupedExpenseCategoriesAllTimeUseCase: ObserveGroupedExpenseCategoriesAllTimeUseCase,
    private val observeExpensesFilteringUseCase: ObserveExpensesFilteringUseCase,
    private val observeExpensesSortingUseCase: ObserveExpensesSortingUseCase,
): ObserveExpensesUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>,
        sortTypeFlow: Flow<ExpensesSortType>,
    ): Flow<Async<ExpensesUiState>> = combineUiState(
        observeGroupedExpensesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedExpenseCategoriesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedExpenseCategoriesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedExpenseCategoriesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeExpensesFilteringUseCase(),
        observeExpensesSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            ExpensesUiState(isLoading = true)
        },
        onError = {
            ExpensesUiState(hasError = true)
        },
        onSuccess = { expenses, categoriesMonthly, categoriesYearly, categoriesAllTime, filtering, sorting, (searchQuery, filterType, sortType) ->
            ExpensesUiState(
                expenses = expenses,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                isLoading = false,
                filteringUiInfo = getFilterUiInfo(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

    private fun getFilterUiInfo(
        searchQuery: String,
        filterType: ExpensesFilterType,
        filterMenuItems: List<ExpensesFilterMenuItem>,
        sortType: ExpensesSortType,
        sortMenuItems: List<ExpensesSortMenuItem>,
    ): ExpensesFilteringUiInfo {
        val showFilterBadge = filterType != ALL_EXPENSES
        val showSortBadge = sortType != ExpensesSortType.BY_DATE

        return when (filterType) {
            ALL_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.all_expenses,
                    emptyLabel = R.string.no_expenses,
                    searchQuery = searchQuery,
                    filterMenuItems = filterMenuItems,
                    showFilterBadge = showFilterBadge,
                    sortMenuItems = sortMenuItems,
                    showSortBadge = showSortBadge,
                )
            }

            DEBIT_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.debit_expenses,
                    emptyLabel = R.string.no_debit_expenses,
                    searchQuery = searchQuery,
                    filterMenuItems = filterMenuItems,
                    showFilterBadge = showFilterBadge,
                    sortMenuItems = sortMenuItems,
                    showSortBadge = showSortBadge,
                )
            }

            COST_EXPENSES -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.cost_expenses,
                    emptyLabel = R.string.no_cost_expenses,
                    searchQuery = searchQuery,
                    filterMenuItems = filterMenuItems,
                    showFilterBadge = showFilterBadge,
                    sortMenuItems = sortMenuItems,
                    showSortBadge = showSortBadge,
                )
            }

            EXCLUDED_FROM_ANALYTICS -> {
                ExpensesFilteringUiInfo(
                    currentFilteringLabel = R.string.exclude_from_analytics,
                    emptyLabel = R.string.no_excluded_from_analytics_expenses,
                    searchQuery = searchQuery,
                    filterMenuItems = filterMenuItems,
                    showFilterBadge = showFilterBadge,
                    sortMenuItems = sortMenuItems,
                    showSortBadge = showSortBadge,
                )
            }
        }
    }

}