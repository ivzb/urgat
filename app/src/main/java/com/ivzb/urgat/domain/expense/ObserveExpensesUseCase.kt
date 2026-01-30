package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveExpensesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<ExpensesFilterType>? = null,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Expense>>>

}

class DefaultObserveExpensesUseCase @Inject constructor(
    repository: ExpensesRepository
): ObserveExpensesUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        filter = ::filterExpenses,
        sort = ::sortExpenses,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        sortTypeFlow: Flow<ExpensesSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Expense>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}