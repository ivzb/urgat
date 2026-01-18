package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import java.math.BigDecimal

fun filterBudgets(items: List<Budget>, filterType: BudgetsFilterType): List<Budget> {
    if (filterType == BudgetsFilterType.ALL_BUDGETS) {
        return items
    }

    return items.filter {
        when (filterType) {
            BudgetsFilterType.ALL_BUDGETS -> true
            BudgetsFilterType.NEGATIVE_BUDGETS -> it.isNegative
            BudgetsFilterType.POSITIVE_BUDGETS -> it.isPositive
        }
    }
}