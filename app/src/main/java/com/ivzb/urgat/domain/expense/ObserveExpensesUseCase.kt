package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.expenses.ExpensesFilterMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveExpensesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterMenuItem?>?,
        limit: Int
    ): Flow<Async<List<Expense>>>

}

class DefaultObserveExpensesUseCase @Inject constructor(
    repository: ExpensesRepository
): ObserveExpensesUseCase, BaseObserveUseCase<Expense, ExpensesFilterMenuItem?>(repository) {

    override val errorMessage: Int = R.string.error_expenses

    override fun filter(items: List<Expense>, filteringType: ExpensesFilterMenuItem?): List<Expense> {
        val type = filteringType?.type ?: ExpensesFilterType.ALL_EXPENSES

        if (type == ExpensesFilterType.ALL_EXPENSES) {
            return items
        }

        return items.filter {
            when (type) {
                ExpensesFilterType.ALL_EXPENSES -> true
                ExpensesFilterType.COST_EXPENSES -> it.isCost
                ExpensesFilterType.DEBIT_EXPENSES -> it.isDebit
            }
        }
    }

}