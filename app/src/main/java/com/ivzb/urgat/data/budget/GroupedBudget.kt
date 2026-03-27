package com.ivzb.urgat.data.budget

import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import java.math.BigDecimal

data class GroupedBudget(
    val category: GroupedBudgetCategories,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val totalInvestments: List<InvestmentTotal>,
    val totalSavings: BigDecimal,
) {

    val remaining: BigDecimal by lazy {
        totalIncome - totalExpenses - totalInvestmentsCost - totalSavings
    }

    val total: BigDecimal by lazy {
        totalIncome.coerceAtLeast(totalExpenses + totalInvestmentsCost + totalSavings)
    }

    private val totalInvestmentsCost by lazy {
        // todo: convert investment currency to budget currency for correct calculation
        totalInvestments.sumOf { it.costPrice }
    }

}