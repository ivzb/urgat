package com.ivzb.urgat.data.expense

import java.math.BigDecimal

data class Expense(
    val id: String,
    val name: String,
    val amount: BigDecimal = BigDecimal.ZERO,
    val category: String = "",
    val date: Long = 0,
    val isDeleted: Boolean = false,
    val createdAt: Long = 0
)