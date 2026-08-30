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

    /**
     * What [accumulatedStash] was paid for.
     *
     * Only the investments can tell the two apart - a trade or a saving is worth exactly what went
     * into it - so this defaults to the stash itself.
     */
    val accumulatedStashCost: BigDecimal = accumulatedStash,

    val stashBreakdown: List<StashBreakdownItem> = emptyList(),
    val savingsRate: BigDecimal?,
    val target: BigDecimal?,

    val withdrawalRate: BigDecimal?,
    val returnOfInvestment: BigDecimal?,
    val taxRate: BigDecimal?,

    val yearsUntilFiRe: BigDecimal?,

    val withdrawalRatePlaceholder: BigDecimal = DEFAULT_FIRE_WITHDRAWAL_RATE,
    val returnOfInvestmentPlaceholder: BigDecimal = DEFAULT_FIRE_RETURN_OF_INVESTMENT,
    val taxRatePlaceholder: BigDecimal = DEFAULT_FIRE_TAX_RATE,

    /**
     * `true` when a quote or an exchange rate could not be resolved while building this.
     *
     * Anything that could not be converted contributes nothing to [accumulatedStash] and anything
     * without a quote is valued at cost, so the stash is then a floor - never an overstatement.
     */
    val isPartial: Boolean = false,
)