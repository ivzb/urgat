package com.ivzb.urgat.data.saving

import java.math.BigDecimal

data class SavingCategory(
    val id: String,
    val name: String,
    val value: BigDecimal,

    val savings: List<Saving>,
)