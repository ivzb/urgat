package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.ui.budgets.BudgetsSortType

fun sortBudgets(items: List<Budget>, sortType: BudgetsSortType): List<Budget> {
    return when (sortType) {
        BudgetsSortType.BY_DATE -> items.sortedByDescending { it.date }
        BudgetsSortType.BY_MIN -> items.sortedWith(compareBy<Budget> { it.total }.thenBy { it.income }.thenBy { it.bankStart })
        BudgetsSortType.BY_MAX -> items.sortedWith(compareByDescending<Budget> { it.total }.thenByDescending { it.income }.thenByDescending { it.bankStart })
    }
}