package com.ivzb.urgat.domain.widget

import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.domain.fire.ObserveNetWorthUseCase
import com.ivzb.urgat.domain.watchlist.ObserveWatchlistAssetsUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * Drops the cached quotes and waits for fresh ones to arrive.
 *
 * This is what "refreshing" actually means, in the one place both the refresh button and the
 * background worker can share. Neither can simply ask for a price - a price is only ever fetched
 * while [MarketPriceRepository] is being asked for one, and that only happens when an observed
 * flow emits. Clearing the cache is what makes those flows re-collect, and collecting them is in
 * turn what fetches.
 *
 * The caller decides how long to wait, because a finger on a button and a job in the background
 * are owed very different patience.
 */
interface RefreshQuotesUseCase {

    /**
     * @param types which widgets' data to wait for - an empty set does nothing
     * @return `true` when every type produced a value before [timeoutMillis] ran out
     */
    suspend operator fun invoke(types: Set<WidgetType>, timeoutMillis: Long): Boolean

}

class DefaultRefreshQuotesUseCase @Inject constructor(
    private val forexRepository: ForexRepository,
    private val marketPriceRepository: MarketPriceRepository,
    private val observeNetWorthUseCase: ObserveNetWorthUseCase,
    private val observeWatchlistAssetsUseCase: ObserveWatchlistAssetsUseCase,
    private val financeRepository: FinanceRepository,
) : RefreshQuotesUseCase {

    override suspend fun invoke(types: Set<WidgetType>, timeoutMillis: Long): Boolean {
        if (types.isEmpty()) {
            return true
        }

        // only the net worth converts between currencies, so only it has any use for the rates
        // being dropped - and they go first, so a recalculation can never pick a stale one up
        if (WidgetType.NET_WORTH in types) {
            forexRepository.clearCache()
        }

        marketPriceRepository.clearCache()

        val refreshed = withTimeoutOrNull(timeoutMillis) {
            coroutineScope {
                types.map { type -> async { awaitQuotes(type) } }.awaitAll()
            }
        }

        return refreshed != null
    }

    /** Collecting is what fetches, so this waits for the first non-loading emission. */
    private suspend fun awaitQuotes(type: WidgetType) {
        when (type) {
            WidgetType.NET_WORTH -> observeNetWorthUseCase()
                .first { it !is Async.Loading }

            // the very same assets the watchlist widget shows, so the same prices get fetched
            WidgetType.WATCHLIST -> observeWatchlistAssetsUseCase(
                sortTypeFlow = financeRepository.watchlistAssetsSortTypeFlow,
            ).first { it !is Async.Loading }
        }
    }

}

