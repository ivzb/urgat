package com.ivzb.urgat.data.budget

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.Expense.Companion.format
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

    companion object {

        fun total(budgets: List<Budget>): BigDecimal =
            budgets.format()

        fun List<Budget>.format(): BigDecimal =
            this.sumOf { it.income }

    }

}