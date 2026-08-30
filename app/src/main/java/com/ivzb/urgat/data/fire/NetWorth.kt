package com.ivzb.urgat.data.fire

import com.ivzb.urgat.util.calculatePnL
import java.math.BigDecimal

/**
 * The accumulated stash (investments + trades + savings) converted to the budget currency.
 */
data class NetWorth(
    val amount: BigDecimal,
    val currency: String,

    /**
     * What [amount] was paid for - the distance between the two is the unrealised profit.
     *
     * Trades and savings carry their own cost, so the investments are the only part of the stash
     * which can move it away from [amount].
     */
    val costPrice: BigDecimal = amount,

    val breakdown: List<StashBreakdownItem> = emptyList(),

    /**
     * `true` when some quotes or exchange rates could not be resolved, which makes [amount] a
     * floor rather than the real number. Worth telling the reader about.
     */
    val isPartial: Boolean = false,
) {

    /**
     * How far [amount] has grown past [costPrice], `null` when there is nothing to compare to.
     */
    val pnl: BigDecimal? = calculatePnL(costPrice, amount)

}

