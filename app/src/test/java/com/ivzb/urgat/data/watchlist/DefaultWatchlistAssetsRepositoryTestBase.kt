package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class DefaultWatchlistAssetsRepositoryTestBase {

    private val dispatcher = StandardTestDispatcher()
    protected lateinit var dao: FakeWatchlistAssetDao
    protected lateinit var repository: DefaultWatchlistAssetsRepository

    protected val fakeMarketPriceRepository = FakeMarketPriceRepository()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeWatchlistAssetDao()
        repository = DefaultWatchlistAssetsRepository(
            localDataSource = dao,
            marketPriceRepository = fakeMarketPriceRepository,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    protected fun givenMarketPrice(
        symbol: String,
        description: String,
        price: String,
        previousClosePrice: String,
        currency: String = "USD",
        pricePrecision: Int = 2,
    ) {
        fakeMarketPriceRepository.prices[symbol] = MarketPrice(
            symbol = symbol,
            description = description,
            price = BigDecimal(price),
            currency = currency,
            previousClosePrice = BigDecimal(previousClosePrice),
            pricePrecision = pricePrecision,
        )
    }
}
