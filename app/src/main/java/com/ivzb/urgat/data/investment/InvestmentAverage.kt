package com.ivzb.urgat.data.investment

/**
 * Average monthly investments of a period.
 *
 * [totals] - average of the whole period, per currency
 * [categories] - average per category name, spread over the same period
 * [assets] - average per asset name, spread over the same period
 */
data class InvestmentAverage(
    val totals: List<InvestmentTotal> = listOf(),
    val categories: Map<String, List<InvestmentTotal>> = mapOf(),
    val assets: Map<String, List<InvestmentTotal>> = mapOf(),
) {

    companion object {

        val EMPTY = InvestmentAverage()

    }

}

