package com.ivzb.urgat.data.expense.useCase

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseObserveUseCase
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.ALL_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.COST_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.DEBIT_EXPENSES
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

interface ObserveExpensesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limit: Int
    ): Flow<Async<List<Expense>>>

}

class DefaultObserveExpensesUseCase @Inject constructor(
    expensesRepository: ExpensesRepository
): ObserveExpensesUseCase, BaseObserveUseCase<Expense, ExpensesFilterType>(expensesRepository) {

    override val errorMessage: Int = R.string.error_expenses

    override fun filter(items: List<Expense>, filteringType: ExpensesFilterType): List<Expense> {
        if (filteringType == ALL_EXPENSES) {
            return items
        }

        return items.filter {
            when (filteringType) {
                ALL_EXPENSES -> true
                COST_EXPENSES -> it.amount > BigDecimal.ZERO
                DEBIT_EXPENSES -> it.amount < BigDecimal.ZERO
            }
        }
    }

}