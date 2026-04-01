package com.ivzb.urgat.data.investmentMarketPrice

import java.math.BigDecimal

interface InvestmentMarketPriceRepository {

    suspend fun calculateMarketPrice(symbol: String, currency: String): BigDecimal?

    suspend fun calculateMarketPrice(symbol: String, currency: String, shares: BigDecimal): BigDecimal?

}

typealias CalculateMarketPrice = suspend (symbol: String, currency: String) -> BigDecimal?
