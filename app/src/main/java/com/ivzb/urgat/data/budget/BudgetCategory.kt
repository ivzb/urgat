package com.ivzb.urgat.data.budget

import java.math.BigDecimal

data class BudgetCategory(
    val id: String,
    val name: String,
    val value: BigDecimal,
)