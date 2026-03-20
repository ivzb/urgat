package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class YahooFinanceMarketPriceRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val api: YahooFinanceApi,
) : MarketPriceRepository {

    override suspend fun fetchPrice(symbol: String): MarketPrice = withContext(dispatcher) {
        val meta = api.fetch(symbol).chart.result.first().meta

        MarketPrice(
            symbol = symbol,
            price = BigDecimal(meta.regularMarketPrice),
            currency = meta.currency,
        )
    }
}
