package com.ivzb.urgat.data.investmentMarketPrice

import java.math.BigDecimal

class FakeInvestmentMarketPriceRepository : InvestmentMarketPriceRepository {

    val prices = mutableMapOf<String, BigDecimal>()
    var shouldThrow = false
    val failingSymbols = mutableSetOf<String>()

    override suspend fun calculateMarketPrice(symbol: String, currency: String): BigDecimal {
        if (shouldThrow || symbol in failingSymbols) {
            throw RuntimeException("Failed to calculate market price for $symbol")
        }

        return prices[symbol] ?: BigDecimal.ZERO
    }

    override suspend fun calculateMarketPrice(symbol: String, currency: String, shares: BigDecimal): BigDecimal {
        if (shouldThrow || symbol in failingSymbols) {
            throw RuntimeException("Failed to calculate market price for $symbol")
        }

        val pricePerShare = prices[symbol] ?: return BigDecimal.ZERO
        return pricePerShare * shares
    }
}

