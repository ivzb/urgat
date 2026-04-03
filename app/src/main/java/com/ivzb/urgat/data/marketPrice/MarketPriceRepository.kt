package com.ivzb.urgat.data.marketPrice

import kotlinx.coroutines.flow.Flow

interface MarketPriceRepository {

    val refreshFlow: Flow<Unit>

    suspend fun fetchPrice(symbol: String): MarketPrice?

    suspend fun clearCache()

}