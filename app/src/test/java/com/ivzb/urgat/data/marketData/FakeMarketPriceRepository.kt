package com.ivzb.urgat.data.marketData

import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository

class FakeMarketPriceRepository : MarketPriceRepository {

    val prices = mutableMapOf<String, MarketPrice>()
    var shouldThrow = false
    val failingSymbols = mutableSetOf<String>()
    var fetchCount = 0
        private set

    override suspend fun fetchPrice(symbol: String): MarketPrice? {
        fetchCount++

        if (shouldThrow || symbol in failingSymbols) {
            return null
        }

        return prices[symbol]
    }
}