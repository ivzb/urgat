package com.ivzb.urgat.data.budget

import java.math.BigDecimal

data class Budget(
    val id: String,
    val income: BigDecimal,
    val bankStart: BigDecimal,
    val bankEnd: BigDecimal?,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long,
)