package com.ivzb.urgat.data.budget

import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import java.math.BigDecimal

data class GroupedBudget(
    val category: GroupedBudgetCategories,
    val currency: String,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val totalInvestments: BigDecimal,
    val totalSavings: BigDecimal,
) {

    val remaining: BigDecimal by lazy {
        totalIncome - totalExpenses - totalInvestments - totalSavings
    }

    val total: BigDecimal by lazy {
        totalIncome.coerceAtLeast(totalExpenses + totalInvestments + totalSavings)
    }

}