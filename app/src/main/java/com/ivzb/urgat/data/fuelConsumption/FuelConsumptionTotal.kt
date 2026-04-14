package com.ivzb.urgat.data.fuelConsumption

import com.ivzb.urgat.data.expense.ExpenseTotal
import java.math.BigDecimal
import kotlin.collections.component1
import kotlin.collections.component2

data class FuelConsumptionTotal(
    val type: String,
    val amount: BigDecimal,
) {

    companion object {

        fun List<FuelConsumptionTotal>.sumByType(): List<FuelConsumptionTotal> =
            groupBy { it.type }
                .map { (type, totals) ->
                    FuelConsumptionTotal(
                        type = type,
                        amount = totals.sumOf { it.amount },
                    )
                }
    }

}