package com.ivzb.urgat.data.saving

import java.math.BigDecimal

data class SavingTotal(
    val amount: BigDecimal,
    val currency: String,
) {

    companion object {

        fun List<SavingTotal>.sumByCurrency(): List<SavingTotal> =
            groupBy { it.currency }
                .map { (currency, totals) ->
                    SavingTotal(
                        amount = totals.sumOf { it.amount },
                        currency = currency,
                    )
                }
    }
}

