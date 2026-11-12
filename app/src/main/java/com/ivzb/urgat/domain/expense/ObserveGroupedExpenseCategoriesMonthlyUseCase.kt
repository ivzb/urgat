package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.GroupExpenses.groupExpenseCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

typealias GroupedExpenseCategories = GroupedItems<String, List<ExpenseTotal>, ExpenseCategory>

interface ObserveGroupedExpenseCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>>

}

class DefaultObserveGroupedExpenseCategoriesMonthlyUseCase @Inject constructor(
    expensesRepository: ExpensesRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedExpenseCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = expensesRepository,
        dispatcher = dispatcher,
        filter = ::filterExpenses,
        sort = ::sortExpenses,
        group = { expenses -> groupExpenseCategoriesMonthly(expenses, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
