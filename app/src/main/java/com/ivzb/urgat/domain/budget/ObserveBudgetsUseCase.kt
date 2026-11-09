package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveBudgetsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<BudgetsFilterType>? = null,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Budget>>>

}

class DefaultObserveBudgetsUseCase @Inject constructor(
    repository: BudgetsRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveBudgetsUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterBudgets,
        sort = ::sortBudgets,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Budget>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
