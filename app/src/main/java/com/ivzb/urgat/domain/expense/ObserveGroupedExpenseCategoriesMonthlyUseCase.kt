package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupExpenseCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

typealias GroupedExpenseCategories = GroupedItems<String, BigDecimal, ExpenseCategory>

interface ObserveGroupedExpenseCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>>

}

class DefaultObserveGroupedExpenseCategoriesMonthlyUseCase @Inject constructor(
    expensesRepository: ExpensesRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedExpenseCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = expensesRepository,
        filter = ::filterExpenses,
        group = { expenses -> groupExpenseCategoriesMonthly(expenses, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenseCategories>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}
