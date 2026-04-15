package com.ivzb.urgat.data.fuelConsumption

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.ivzb.urgat.util.calculateFuelConsumption
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency
import java.math.BigDecimal

data class FuelConsumption(
    val id: String,
    val amount: BigDecimal,
    val price: BigDecimal,
    val currency: String,
    val type: String,
    val carName: String,
    val odometer: Int,
    val distance: Int?,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val fuelConsumption = calculateFuelConsumption(amount, distance)

    val summary: AnnotatedString =
        buildAnnotatedString {
            append("${amount.format()}L $type")
            append(" | ")
            append("${price.format()} ${currency.formatCurrency()}")

            fuelConsumption?.let {
                append(" | ")
                append("${it.format(digits = 1)}L/100km")
            }
        }

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

