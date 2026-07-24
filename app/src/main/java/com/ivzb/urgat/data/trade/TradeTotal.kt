package com.ivzb.urgat.data.trade

import java.math.BigDecimal

data class TradeTotal(
    val currency: String,
    val price: BigDecimal,
) {

    companion object {

        fun List<TradeTotal>.sumByCurrency(): List<TradeTotal> =
            groupBy { it.currency }
                .map { (currency, totals) ->
                    TradeTotal(
                        currency = currency,
                        price = totals.sumOf { it.price },
                    )
                }
    }
}

