package com.ivzb.urgat.data.expense

/**
 * Average monthly expenses of a period.
 *
 * [totals] - average of the whole period, per currency
 * [categories] - average per category name, spread over the same period
 */
data class ExpenseAverage(
    val totals: List<ExpenseTotal> = listOf(),
    val categories: Map<String, List<ExpenseTotal>> = mapOf(),
) {

    companion object {

        val EMPTY = ExpenseAverage()

    }

}

