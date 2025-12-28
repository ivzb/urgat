package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.domain.common.Group.groupExpenseCategoriesAllTime
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedExpenseCategoriesAllTimeUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<ExpenseCategory>>>

}

class DefaultObserveGroupedExpenseCategoriesAllTimeUseCase @Inject constructor(
    repository: ExpensesRepository
): ObserveGroupedExpenseCategoriesAllTimeUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterExpenses,
        group = ::groupExpenseCategoriesAllTime,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<ExpenseCategory>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}
