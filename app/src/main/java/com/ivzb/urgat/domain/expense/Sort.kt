package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType

fun sortExpenses(items: List<Expense>, sortType: ExpensesSortType): List<Expense> {
    return when (sortType) {
        ExpensesSortType.BY_DATE -> items.sortedByDescending { it.date }
        ExpensesSortType.BY_COST -> items.sortedByDescending { it.amount }
    }
}