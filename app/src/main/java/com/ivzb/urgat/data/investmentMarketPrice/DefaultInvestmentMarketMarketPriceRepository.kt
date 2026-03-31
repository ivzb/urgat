package com.ivzb.urgat.data.investmentMarketPrice

import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.util.normalizeInstrument
import java.math.BigDecimal
import javax.inject.Inject

class DefaultInvestmentMarketMarketPriceRepository @Inject constructor(
    private val marketPriceRepository: MarketPriceRepository,
    private val forexRepository: ForexRepository,
) : InvestmentMarketPriceRepository {

    override suspend fun calculateMarketPrice(symbol: String, currency: String): BigDecimal? {
        val marketPrice = marketPriceRepository.fetchPrice(symbol) ?: return null
        val sameCurrency = normalizeInstrument(currency).equals(marketPrice.currency, ignoreCase = true)

        return if (sameCurrency) {
            marketPrice.price
        } else {
            forexRepository.convert(
                amount = marketPrice.price,
                fromCurrency = marketPrice.currency,
                toCurrency = currency,
            )
        }
    }

    override suspend fun calculateMarketPrice(symbol: String, currency: String, shares: BigDecimal): BigDecimal? {
        val marketPrice = calculateMarketPrice(symbol, currency) ?: return null

        return marketPrice * shares
    }

}
