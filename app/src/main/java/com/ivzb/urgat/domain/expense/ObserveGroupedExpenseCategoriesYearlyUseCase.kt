package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupExpenseCategoriesYearly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedExpenseCategoriesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>>

}

class DefaultObserveGroupedExpenseCategoriesYearlyUseCase @Inject constructor(
    expensesRepository: ExpensesRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedExpenseCategoriesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = expensesRepository,
        filter = ::filterExpenses,
        group = { expenses -> groupExpenseCategoriesYearly(expenses, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}
