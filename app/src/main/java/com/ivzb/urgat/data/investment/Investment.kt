package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class Investment(
    val id: String,
    val name: String,
    val currency: String,
    val amount: BigDecimal = BigDecimal.ZERO,
    val category: String = "",
    val isDeleted: Boolean = false,
    val createdAt: Long = 0
)