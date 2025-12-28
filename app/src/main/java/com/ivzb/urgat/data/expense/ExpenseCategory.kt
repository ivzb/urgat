package com.ivzb.urgat.data.expense

import java.math.BigDecimal

data class ExpenseCategory(
    val id: String,
    val name: String,
    val value: BigDecimal,
)