package com.ivzb.urgat.data.fire

import java.math.BigDecimal

/**
 * The accumulated stash (investments + trades + savings) converted to the budget currency.
 */
data class NetWorth(
    val amount: BigDecimal,
    val currency: String,
    val breakdown: List<StashBreakdownItem> = emptyList(),
)

