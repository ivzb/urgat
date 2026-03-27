package com.ivzb.urgat.data.marketPrice

interface MarketPriceRepository {

    suspend fun fetchPrice(symbol: String): MarketPrice?

}
