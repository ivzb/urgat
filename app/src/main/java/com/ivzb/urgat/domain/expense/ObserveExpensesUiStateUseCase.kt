package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
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
            val allExpenses = categoriesAllTime.flatMap { it.expenses }

            ExpensesUiState(
                expenses = expenses,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                averagesMonthly = Group.groupExpenseAveragesMonthly(allExpenses),
                averagesYearly = Group.groupExpenseAveragesYearly(allExpenses),
                averageAllTime = Group.groupExpenseAverageAllTime(allExpenses),
                isLoading = false,
                filteringUiInfo = ExpensesFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

}

