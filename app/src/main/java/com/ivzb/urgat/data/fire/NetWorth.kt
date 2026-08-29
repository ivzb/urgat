package com.ivzb.urgat.data.fire

import java.math.BigDecimal

/**
 * The accumulated stash (investments + trades + savings) converted to the budget currency.
 */
data class NetWorth(
    val amount: BigDecimal,
    val currency: String,
    val breakdown: List<StashBreakdownItem> = emptyList(),

    /**
     * `true` when some quotes or exchange rates could not be resolved, which makes [amount] a
     * floor rather than the real number. Worth telling the reader about.
     */
    val isPartial: Boolean = false,
)

