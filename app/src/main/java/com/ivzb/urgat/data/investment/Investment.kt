package com.ivzb.urgat.data.investment

import java.math.BigDecimal

data class Investment(
    val id: String,
    val name: String,
    val amount: BigDecimal,
    val cost: BigDecimal,
    val category: String,
    val currency: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)