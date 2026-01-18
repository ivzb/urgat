package com.ivzb.urgat.data.budget

import java.math.BigDecimal

data class Budget(
    val id: String,
    val income: BigDecimal,
    val bankStart: BigDecimal,
    val bankEnd: BigDecimal?,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val total: BigDecimal?
        get () {
            if (bankEnd == null) {
                return null
            }

            return bankEnd - bankStart + income
        }

    val isNegative: Boolean
        get() {
            val total = total
            return total != null && total < BigDecimal.ZERO
        }

    val isPositive: Boolean
        get() {
            val total = total
            return total != null && total >= BigDecimal.ZERO
        }

    fun formatCsv(): String =
        "\"$id\", \"$income\", \"$bankStart\", \"$bankEnd\", $date, $createdAt"

}