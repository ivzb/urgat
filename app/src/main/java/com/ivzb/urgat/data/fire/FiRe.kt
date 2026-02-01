package com.ivzb.urgat.data.fire

import java.math.BigDecimal

data class FiRe(
    val selection: String,

    val monthlyIncome: BigDecimal,
    val monthlyExpenses: BigDecimal,
    val monthlyInvestments: BigDecimal,

    val annualIncome: BigDecimal,
    val annualExpenses: BigDecimal,
    val annualInvestments: BigDecimal,

    val accumulatedStash: BigDecimal,
    val savingsRate: BigDecimal,
    val target: BigDecimal,

    val withdrawalRate: BigDecimal,
    val returnOfInvestments: BigDecimal,
    val taxRate: BigDecimal,

    val yearsUntilFiRe: BigDecimal,
)