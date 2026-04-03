package com.ivzb.urgat.data.marketData

import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeMarketPriceRepository : MarketPriceRepository {

    val prices = mutableMapOf<String, MarketPrice>()
    var shouldThrow = false
    val failingSymbols = mutableSetOf<String>()
    var fetchCount = 0
        private set

    private val _refreshFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val refreshFlow: Flow<Unit> = _refreshFlow.asSharedFlow()

    override suspend fun fetchPrice(symbol: String): MarketPrice? {
        fetchCount++

        if (shouldThrow || symbol in failingSymbols) {
            return null
        }

        return prices[symbol]
    }

    override suspend fun clearCache() {
        prices.clear()
        _refreshFlow.emit(Unit)
    }

    suspend fun emitRefresh() {
        _refreshFlow.emit(Unit)
    }
}