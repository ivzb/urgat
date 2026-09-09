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
     * The names of the positions whose quote could not be fetched, so they are valued at cost.
     */
    val unavailableAssets: List<String> = emptyList(),

    /**
     * The currency pairs whose exchange rate could not be resolved, written as `USD -> EUR`.
     */
    val unavailableRates: List<String> = emptyList(),
) {

    /**
     * `true` when some quotes or exchange rates could not be resolved.
     *
     * An unfetched quote values a position at what it cost, so [amount] would read the buy price
     * back as the net worth - readers show that the prices are unavailable instead of the number.
     */
    val isPartial: Boolean
        get() = unavailableAssets.isNotEmpty() || unavailableRates.isNotEmpty()

    /**
     * Everything which held the stash back, the positions first.
     *
     * A reader with room for one line only wants to name the assets before the currency pairs -
     * a missing quote is something the user recognises, a missing rate is not.
     */
    val unavailable: List<String>
        get() = unavailableAssets + unavailableRates

    /**
     * How far [amount] has grown past [costPrice], `null` when there is nothing to compare to.
     */
    val pnl: BigDecimal? = calculatePnL(costPrice, amount)

}

