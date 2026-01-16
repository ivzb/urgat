package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveBudgetsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        filterTypeFlow: Flow<BudgetsFilterType>? = null,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Budget>>>

}

class DefaultObserveBudgetsUseCase @Inject constructor(
    repository: BudgetsRepository
): ObserveBudgetsUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        filter = ::filterBudgets,
        sort = ::sortBudgets,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Budget>>> = observeUseCase(searchQueryFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}