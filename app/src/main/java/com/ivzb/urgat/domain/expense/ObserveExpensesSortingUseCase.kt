package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveExpensesSortingUseCase {

    operator fun invoke(): Flow<Async<List<ExpensesSortMenuItem>>>

}

class DefaultObserveExpensesSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveExpensesSortingUseCase {

    override fun invoke(): Flow<Async<List<ExpensesSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = ExpensesSortType.BY_MIN, labelResId = R.string.sort_by_min),
                SortMenuItem(type = ExpensesSortType.BY_MAX, labelResId = R.string.sort_by_max),
            )
        )
    ).flowOn(dispatcher)

}
