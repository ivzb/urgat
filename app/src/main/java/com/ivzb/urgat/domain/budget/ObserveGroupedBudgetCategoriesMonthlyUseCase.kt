package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group.groupBudgetCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.Async
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

typealias GroupedBudgetCategories = GroupedItems<String, BigDecimal, BudgetCategory>

interface ObserveGroupedBudgetCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedBudgetCategories>>>

}

class DefaultObserveGroupedBudgetCategoriesMonthlyUseCase @Inject constructor(
    budgetsRepository: BudgetsRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedBudgetCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = budgetsRepository,
        dispatcher = dispatcher,
        filter = ::filterBudgets,
        sort = ::sortBudgets,
        group = { budgets -> groupBudgetCategoriesMonthly(budgets, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedBudgetCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
