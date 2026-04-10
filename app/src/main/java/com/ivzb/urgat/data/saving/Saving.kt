package com.ivzb.urgat.data.saving

import com.ivzb.urgat.util.formatCurrency
import java.math.BigDecimal

data class Saving(
    val id: String,
    val name: String,
    val amount: BigDecimal,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
) {

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$amount\", \"$currency\", $date, $createdAt"

    companion object {

        fun total(savings: List<Saving>): List<SavingTotal> =
            savings
                .groupBy { it.currency.formatCurrency() }
                .map { (currency, savings) ->
                    SavingTotal(
                        amount = savings.sumOf { it.amount },
                        currency = currency,
                    )
                }
                .sortedBy { it.currency }

        suspend fun total(
            savings: List<Saving>?,
            toCurrency: String,
            convert: suspend (amount: BigDecimal, fromCurrency: String, toCurrency: String) -> BigDecimal?,
        ): BigDecimal =
            savings
                ?.mapNotNull { convert(it.amount, it.currency, toCurrency) }
                ?.sumOf { it }
                ?: BigDecimal.ZERO

        fun List<Saving>.format(): BigDecimal =
            this.sumOf { it.amount }

    }

}