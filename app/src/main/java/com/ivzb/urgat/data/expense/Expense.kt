package com.ivzb.urgat.data.expense

import java.math.BigDecimal

data class Expense(
    val id: String,
    val name: String,
    val amount: BigDecimal,
    val category: String,
    val date: Long,
    val isDeleted: Boolean,
    val createdAt: Long
)