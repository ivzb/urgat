package com.ivzb.urgat.data.marketData

import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository

class FakeMarketPriceRepository : MarketPriceRepository {

    val prices = mutableMapOf<String, MarketPrice>()
    var shouldThrow = false
    val failingSymbols = mutableSetOf<String>()

    override suspend fun fetchPrice(symbol: String): MarketPrice {
        if (shouldThrow || symbol in failingSymbols) {
            throw RuntimeException("Failed to fetch price for $symbol")
        }

        return prices[symbol] ?: throw RuntimeException("No price for $symbol")
    }
}