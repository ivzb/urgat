package com.ivzb.urgat.data.weight

import java.math.BigDecimal

data class Weight(
    val id: String,
    val value: BigDecimal,
    val isDeleted: Boolean,
    val createdAt: Long
)
