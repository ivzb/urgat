package com.ivzb.urgat.data.expense.useCase

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseObserveUseCase
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.ALL_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.COST_EXPENSES
import com.ivzb.urgat.ui.expenses.ExpensesFilterType.DEBIT_EXPENSES
import java.math.BigDecimal
import javax.inject.Inject

class ObserveExpensesUseCase @Inject constructor(
    expensesRepository: ExpensesRepository
): BaseObserveUseCase<Expense, ExpensesFilterType>(expensesRepository) {

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