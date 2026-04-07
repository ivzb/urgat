package com.ivzb.urgat.data.saving

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

        fun total(savings: List<Saving>): BigDecimal = savings.format()

        fun List<Saving>.format(): BigDecimal =
            this.sumOf { it.amount }

    }

}