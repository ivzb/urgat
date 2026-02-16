package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveBudgetsUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>,
        sortTypeFlow: Flow<BudgetsSortType>,
    ): Flow<Async<BudgetsUiState>>

}

class DefaultObserveBudgetsUiStateUseCase @Inject constructor(
    private val observeBudgetsUseCase: ObserveBudgetsUseCase,
    private val observeGroupedBudgetsMonthlyUseCase: ObserveGroupedBudgetsMonthlyUseCase,
    private val observeGroupedBudgetsYearlyUseCase: ObserveGroupedBudgetsYearlyUseCase,
    private val observeGroupedBudgetsAllTimeUseCase: ObserveGroupedBudgetsAllTimeUseCase,
    private val observeBudgetsFilteringUseCase: ObserveBudgetsFilteringUseCase,
    private val observeBudgetsSortingUseCase: ObserveBudgetsSortingUseCase,
): ObserveBudgetsUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>,
        sortTypeFlow: Flow<BudgetsSortType>,
    ): Flow<Async<BudgetsUiState>> = combineUiState(
        observeBudgetsUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedBudgetsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedBudgetsYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedBudgetsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeBudgetsFilteringUseCase(),
        observeBudgetsSortingUseCase(),
        searchQueryFlow.map { Async.Success(it) },

        onLoading = {
            BudgetsUiState(isLoading = true)
        },
        onError = {
            BudgetsUiState(hasError = true)
        },
        onSuccess = { budgets, monthlyBudgets, yearlyBudgets, allTimeBudget, filtering, sorting, searchQuery ->
            BudgetsUiState(
                budgets = budgets,
                monthlyBudgets = monthlyBudgets,
                yearlyBudgets = yearlyBudgets,
                allTimeBudget = allTimeBudget,
                filteringUiInfo = BudgetsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_budgets,
                    emptyLabel = if (searchQuery.isEmpty()) R.string.no_budgets_label else R.string.no_budgets_search,
                    emptyMessage = R.string.no_budgets_message,
                    emptyButtonLabel = R.string.add_budget,

                    filterMenuItems = filtering,
                    sortMenuItems = sorting,
                ),

            )
        }
    ).map { Async.Success(it) }

}