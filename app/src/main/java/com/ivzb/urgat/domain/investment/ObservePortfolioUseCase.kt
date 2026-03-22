package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.portfolio.Portfolio
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

interface ObservePortfolioUseCase {
    operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Portfolio>>>
}

class DefaultObservePortfolioUseCase @Inject constructor(
    private val marketPriceRepository: MarketPriceRepository,
    private val observeInvestmentAssetsUseCase: ObserveInvestmentAssetsUseCase,
    @ApplicationScope private val scope: CoroutineScope,
) : ObservePortfolioUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Portfolio>>> =
        observeInvestmentAssetsUseCase(
            searchQueryFlow = searchQueryFlow,
            dateRangeFlow = dateRangeFlow,
            filterTypeFlow = filterTypeFlow,
            sortTypeFlow = sortTypeFlow,
            limitFlow = limitFlow,
        ).map { async ->
            when (async) {
                is Async.Loading -> Async.Loading
                is Async.Error -> Async.Error
                is Async.Success -> Async.Success(fetchPortfolio(async.data))
            }
        }

    private suspend fun fetchPortfolio(assets: List<InvestmentAsset>): List<Portfolio> =
        assets.map { asset ->
            scope.async {
                try {
                    val marketPrice = marketPriceRepository.fetchPrice(asset.name)
                    val sameCurrency = asset.currency.equals(marketPrice.currency, ignoreCase = true)

                    Portfolio(
                        name = asset.name,
                        category = asset.category,
                        shares = asset.shares,
                        costValue = asset.marketValue.takeIf { sameCurrency },
                        marketValue = asset.shares * marketPrice.price,
                        currency = marketPrice.currency,
                    )
                } catch (e: Exception) {
                    Tracker.error(e)

                    Portfolio(
                        name = asset.name,
                        category = asset.category,
                        shares = asset.shares,
                        costValue = null,
                        marketValue = BigDecimal.ZERO,
                        currency = asset.currency,
                    )
                }
            }
        }
        .awaitAll()
}

