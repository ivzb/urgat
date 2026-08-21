package com.ivzb.urgat.data.expense

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.util.div
import com.ivzb.urgat.util.formatCurrency
import com.ivzb.urgat.util.toInt
import com.ivzb.urgat.util.toLocalDate
import java.math.BigDecimal
import java.time.YearMonth

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

        /**
         * Amount of distinct months the given [expenses] span over.
         */
        fun months(expenses: List<Expense>): Int =
            expenses
                .filterNot { it.excludeFromAnalytics }
                .map { YearMonth.from(it.date.toLocalDate()) }
                .distinct()
                .size

        /**
         * Average monthly totals per currency of the given [expenses],
         * spread over [months] (defaults to the months the [expenses] span over).
         */
        fun averageMonthly(
            expenses: List<Expense>,
            months: Int = months(expenses),
        ): List<ExpenseTotal> =
            if (months <= 0) {
                listOf()
            } else {
                total(expenses).map { total ->
                    ExpenseTotal(
                        amount = total.amount / months.toBigDecimal(),
                        currency = total.currency,
                    )
                }
            }

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