package com.ivzb.urgat.data.investmentMarketPrice

import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import java.math.BigDecimal
import javax.inject.Inject

class DefaultInvestmentMarketMarketPriceRepository @Inject constructor(
    private val marketPriceRepository: MarketPriceRepository,
    private val forexRepository: ForexRepository,
) : InvestmentMarketPriceRepository {

    override suspend fun calculateMarketPrice(symbol: String, currency: String, shares: BigDecimal): BigDecimal {
        val marketPrice = marketPriceRepository.fetchPrice(symbol)
        val sameCurrency = currency.equals(marketPrice.currency, ignoreCase = true)

        return if (sameCurrency) {
            shares * marketPrice.price
        } else {
            forexRepository.convert(
                amount = shares * marketPrice.price,
                fromCurrency = marketPrice.currency,
                toCurrency = currency,
            )
        }
    }

}
