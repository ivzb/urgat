package com.ivzb.urgat.data.fire

import java.math.BigDecimal

data class FiRe(
    val lastMonthIncome: BigDecimal,
    val lastMonthExpenses: BigDecimal,
    val lastMonthInvestments: BigDecimal,
    val lastMonthSavings: BigDecimal,

    val allTimeInvestments: BigDecimal,
    val allTimeSavings: BigDecimal,
) {

    val target: BigDecimal = lastMonthExpenses * (12 * 25).toBigDecimal()

    val remaining: BigDecimal = (target - (lastMonthInvestments - lastMonthSavings) * 12.toBigDecimal()).coerceAtLeast(0.toBigDecimal())

}