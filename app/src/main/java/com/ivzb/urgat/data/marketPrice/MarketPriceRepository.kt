package com.ivzb.urgat.data.marketPrice

import kotlinx.coroutines.flow.Flow

interface MarketPriceRepository {

    val refreshFlow: Flow<Unit>

    /**
     * When a price was last fetched, `null` until the first one is.
     *
     * Prices are cached until [clearCache], so this is how old the data actually is.
     */
    val lastFetchedAt: Long?

    suspend fun fetchPrice(symbol: String): MarketPrice?

    suspend fun clearCache()

}