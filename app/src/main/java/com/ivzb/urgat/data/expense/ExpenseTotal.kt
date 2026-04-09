package com.ivzb.urgat.data.expense

import java.math.BigDecimal

data class ExpenseTotal(
    val amount: BigDecimal,
    val currency: String,
) {

    companion object {

        fun List<ExpenseTotal>.sumByCurrency(): List<ExpenseTotal> =
            groupBy { it.currency }
                .map { (currency, totals) ->
                    ExpenseTotal(
                        amount = totals.sumOf { it.amount },
                        currency = currency,
                    )
                }
    }
}
