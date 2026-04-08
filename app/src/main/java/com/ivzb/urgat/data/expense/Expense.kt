package com.ivzb.urgat.data.expense

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

        fun total(expenses: List<Expense>): BigDecimal = expenses.filterNot { it.excludeFromAnalytics }.format()

        fun List<Expense>.format(): BigDecimal =
            this.sumOf { it.amount }

    }

}