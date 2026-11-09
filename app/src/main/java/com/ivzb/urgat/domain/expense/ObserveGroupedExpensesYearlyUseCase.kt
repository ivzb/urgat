package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveGroupedExpensesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenses>>>

}

class DefaultObserveGroupedExpensesYearlyUseCase @Inject constructor(
    repository: ExpensesRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedExpensesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterExpenses,
        sort = ::sortExpenses,
        group = { items -> Group.byDateYearly(items, Expense::date, Expense::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenses>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
