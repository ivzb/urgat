package com.ivzb.urgat.data.expense

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.util.formatCurrency
import com.ivzb.urgat.util.toInt
import java.math.BigDecimal

data class Expense(
    val id: String,
    val name: String,
    val amount: BigDecimal,
    val currency: String,
    val category: String,
    val date: Long,
    val excludeFromAnalytics: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val isCost by lazy { amount > BigDecimal.ZERO }

    val isDebit by lazy { amount < BigDecimal.ZERO }

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$amount\", \"$currency\", \"$category\", $date, ${excludeFromAnalytics.toInt()}, $createdAt"

    companion object {

        fun total(expenses: List<Expense>): List<ExpenseTotal> =
            expenses
                .filterNot { it.excludeFromAnalytics }
                .groupBy { it.currency.formatCurrency() }
                .map { (currency, expenses) ->
                    ExpenseTotal(
                        amount = expenses.sumOf { it.amount },
                        currency = currency,
                    )
                }
                .sortedBy { it.currency }

        suspend fun total(
            expenses: List<Expense>?,
            toCurrency: String,
            convert: suspend (amount: BigDecimal, fromCurrency: String, toCurrency: String) -> BigDecimal?,
        ): BigDecimal =
            expenses
                ?.mapNotNull { convert(it.amount, it.currency, toCurrency) }
                ?.sumOf { it }
                ?: BigDecimal.ZERO

        fun List<Expense>.format(): BigDecimal =
            this.sumOf { it.amount }

    }

}