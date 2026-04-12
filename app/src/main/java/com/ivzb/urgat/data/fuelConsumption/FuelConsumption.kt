package com.ivzb.urgat.data.fuelConsumption

import com.ivzb.urgat.util.formatCurrency
import java.math.BigDecimal

data class FuelConsumption(
    val id: String,
    val amount: BigDecimal,
    val price: BigDecimal,
    val currency: String,
    val carName: String,
    val odometer: Int,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val total: BigDecimal = amount * price

    fun formatCsv(): String =
        "\"$id\", \"$amount\", \"$price\", \"$currency\", \"$carName\", $odometer, $date, $createdAt"

    companion object {

        fun total(fuelConsumption: List<FuelConsumption>): List<FuelConsumptionTotal> =
            fuelConsumption
                .groupBy { it.currency.formatCurrency() }
                .map { (currency, items) ->
                    FuelConsumptionTotal(
                        amount = items.sumOf { it.total },
                        currency = currency,
                    )
                }
                .sortedBy { it.currency }

    }

}

