package com.ivzb.urgat.data.fire

import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_RETURN_OF_INVESTMENT
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_TAX_RATE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_WITHDRAWAL_RATE
import java.math.BigDecimal

data class FiRe(
    val selection: String,
    val currency: String,

    val monthlyIncome: BigDecimal,
    val monthlyExpenses: BigDecimal,
    val monthlyInvestments: BigDecimal,

    val annualIncome: BigDecimal,
    val annualExpenses: BigDecimal,
    val annualInvestments: BigDecimal,

    val accumulatedStash: BigDecimal,
    val savingsRate: BigDecimal?,
    val target: BigDecimal?,

    val withdrawalRate: BigDecimal?,
    val returnOfInvestment: BigDecimal?,
    val taxRate: BigDecimal?,

    val yearsUntilFiRe: BigDecimal?,

    val withdrawalRatePlaceholder: BigDecimal = DEFAULT_FIRE_WITHDRAWAL_RATE,
    val returnOfInvestmentPlaceholder: BigDecimal = DEFAULT_FIRE_RETURN_OF_INVESTMENT,
    val taxRatePlaceholder: BigDecimal = DEFAULT_FIRE_TAX_RATE,
)