package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveExpensesSortingUseCase {

    operator fun invoke(): Flow<Async<List<ExpensesSortMenuItem>>>

}

class DefaultObserveExpensesSortingUseCase @Inject constructor() : ObserveExpensesSortingUseCase {

    override fun invoke(): Flow<Async<List<ExpensesSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = ExpensesSortType.BY_COST, labelResId = R.string.sort_by_cost),
        )))
    }

}
