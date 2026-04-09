package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

typealias GroupedExpenses = GroupedItems<String, List<ExpenseTotal>, Expense>

interface ObserveGroupedExpensesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenses>>>

}

class DefaultObserveGroupedExpensesMonthlyUseCase @Inject constructor(
    repository: ExpensesRepository
): ObserveGroupedExpensesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterExpenses,
        sort = ::sortExpenses,
        group = { items -> Group.byDateMonthly(items, Expense::date, Expense::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedExpenses>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}