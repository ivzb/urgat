package com.ivzb.urgat.data.fire

import com.ivzb.urgat.util.calculatePnL
import java.math.BigDecimal

data class StashBreakdownItem(
    val name: String,
    val amount: BigDecimal,

    /**
     * What [amount] was bought for.
     *
     * Defaults to [amount] for the parts of the stash which cannot appreciate on their own - a
     * saving or a trade is worth exactly what went into it, so it has no profit to report.
     */
    val costPrice: BigDecimal = amount,
) {

    /**
     * How far [amount] has drifted from [costPrice], `null` when there is nothing to compare to.
     */
    val pnl: BigDecimal? = calculatePnL(costPrice, amount)

}
