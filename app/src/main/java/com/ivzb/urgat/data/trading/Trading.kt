package com.ivzb.urgat.data.trading

import java.math.BigDecimal

data class Trading(
    val id: String,
    val name: String,
    val shares: BigDecimal,
    val marketPrice: BigDecimal,
    val fee: BigDecimal,
    val currency: String,
    val orderType: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
) {

    val total: BigDecimal
        get() = (marketPrice * shares) + fee

    fun formatCsv(): String =
        "\"$id\", \"$name\", \"$shares\", \"$marketPrice\", \"$fee\", \"$currency\", \"$orderType\", $date, $createdAt"

}