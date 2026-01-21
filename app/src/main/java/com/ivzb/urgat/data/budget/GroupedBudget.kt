package com.ivzb.urgat.data.budget

import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import java.math.BigDecimal

data class GroupedBudget(
    val category: GroupedBudgetCategories,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val totalInvestments: BigDecimal,
    val totalInvestmentsFormatted: String,
) {

    val remaining: BigDecimal
        get() = totalIncome - totalExpenses - totalInvestments

    val total: BigDecimal
        get() = totalIncome.coerceAtLeast(totalExpenses + totalInvestments)

}