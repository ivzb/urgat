package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.di.IoDispatcher
import com.ivzb.urgat.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

class YahooFinanceMarketPriceRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val api: YahooFinanceApi,
    private val logger: Logger,
) : MarketPriceRepository {

    override suspend fun fetchPrice(symbol: String): MarketPrice = withContext(dispatcher) {
        val meta = api.fetch(yahooSymbolMapping[symbol] ?: symbol).chart.result.first().meta

        MarketPrice(
            symbol = symbol,
            price = BigDecimal(meta.regularMarketPrice),
            currency = meta.currency,
        ).also {
            logger.debug("market price fetched: $it")
        }
    }

    companion object {

        // replace user symbol with yahoo finance symbol
        private val yahooSymbolMapping = mapOf(
            "BRK.B" to "BRK-B",
            "SXR8" to "SXR8.DE",
            "XAU" to "GC=F",
            "BTC" to "BTC-USD",
            "ETH" to "ETH-USD",
        )

    }

}
