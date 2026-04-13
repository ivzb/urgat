package com.ivzb.urgat.data.fuelConsumption

import java.math.BigDecimal

data class FuelConsumption(
    val id: String,
    val amount: BigDecimal,
    val price: BigDecimal,
    val currency: String,
    val type: String,
    val carName: String,
    val odometer: Int,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    fun formatCsv(): String =
        "\"$id\", \"$amount\", \"$price\", \"$currency\", \"$type\", \"$carName\", $odometer, $date, $createdAt"

    companion object {

        fun total(fuelConsumption: List<FuelConsumption>): List<FuelConsumptionTotal> =
            fuelConsumption
                .groupBy { it.type }
                .map { (type, items) ->
                    FuelConsumptionTotal(
                        type = type,
                        amount = items.sumOf { it.amount },
                    )
                }
                .sortedBy { it.type }

    }

}

