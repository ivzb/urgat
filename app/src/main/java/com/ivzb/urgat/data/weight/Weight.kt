package com.ivzb.urgat.data.weight

import java.math.BigDecimal

data class Weight(
    val id: String,
    val value: BigDecimal,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    fun formatCsv(): String =
        "\"$id\", \"$value\", $date, $createdAt"

}
