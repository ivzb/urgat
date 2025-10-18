package com.ivzb.urgat.data.expense

import java.math.BigDecimal

data class Expense(
    val id: String,
    val name: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
    val isDeleted: Boolean = false,
    val createdAt: Long
)