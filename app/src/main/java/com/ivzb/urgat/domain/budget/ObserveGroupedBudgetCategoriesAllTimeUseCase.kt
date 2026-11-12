package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.GroupBudgets.groupBudgetCategoriesAllTime
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveGroupedBudgetCategoriesAllTimeUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<BudgetCategory>>

}

class DefaultObserveGroupedBudgetCategoriesAllTimeUseCase @Inject constructor(
    budgetsRepository: BudgetsRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedBudgetCategoriesAllTimeUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = budgetsRepository,
        dispatcher = dispatcher,
        filter = ::filterBudgets,
        sort = ::sortBudgets,
        group = { budgets -> groupBudgetCategoriesAllTime(budgets, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<BudgetCategory>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
