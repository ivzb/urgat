package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group.groupExpenseCategoriesYearly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveGroupedExpenseCategoriesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>>

}

class DefaultObserveGroupedExpenseCategoriesYearlyUseCase @Inject constructor(
    expensesRepository: ExpensesRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedExpenseCategoriesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = expensesRepository,
        dispatcher = dispatcher,
        filter = ::filterExpenses,
        sort = ::sortExpenses,
        group = { expenses -> groupExpenseCategoriesYearly(expenses, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
