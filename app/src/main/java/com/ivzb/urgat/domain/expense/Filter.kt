package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.ui.expenses.ExpensesFilterType

fun filterExpenses(items: List<Expense>, filterType: ExpensesFilterType): List<Expense> {
    if (filterType == ExpensesFilterType.ALL_EXPENSES) {
        return items
    }

    return items.filter {
        when (filterType) {
            ExpensesFilterType.ALL_EXPENSES -> true
            ExpensesFilterType.COST_EXPENSES -> it.isCost
            ExpensesFilterType.DEBIT_EXPENSES -> it.isDebit
            ExpensesFilterType.EXCLUDED_FROM_ANALYTICS -> it.excludeFromAnalytics
        }
    }
}