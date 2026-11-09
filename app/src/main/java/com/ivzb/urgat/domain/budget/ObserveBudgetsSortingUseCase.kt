package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.budgets.BudgetsSortMenuItem
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveBudgetsSortingUseCase {

    operator fun invoke(): Flow<Async<List<BudgetsSortMenuItem>>>

}

class DefaultObserveBudgetsSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveBudgetsSortingUseCase {

    override fun invoke(): Flow<Async<List<BudgetsSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = BudgetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = BudgetsSortType.BY_MIN, labelResId = R.string.sort_by_min),
                SortMenuItem(type = BudgetsSortType.BY_MAX, labelResId = R.string.sort_by_max),
            )
        )
    ).flowOn(dispatcher)

}
