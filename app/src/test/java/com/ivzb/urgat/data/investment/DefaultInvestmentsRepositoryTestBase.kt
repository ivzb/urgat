package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.investmentMarketPrice.FakeInvestmentMarketPriceRepository
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
abstract class DefaultInvestmentsRepositoryTestBase {

    private val dispatcher = StandardTestDispatcher()
    protected lateinit var dao: FakeInvestmentDao
    protected lateinit var repository: DefaultInvestmentsRepository

    protected val fakeMarketPriceRepository = FakeMarketPriceRepository()
    protected val fakeInvestmentMarketPriceRepository = FakeInvestmentMarketPriceRepository()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeInvestmentDao()
        repository = DefaultInvestmentsRepository(
            localDataSource = dao,
            marketPriceRepository = fakeMarketPriceRepository,
            investmentMarketPriceRepository = fakeInvestmentMarketPriceRepository,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    protected fun givenMarketPrice(symbol: String, description: String, price: String, pricePrecision: Int = 2) {
        fakeMarketPriceRepository.prices[symbol] = MarketPrice(
            symbol = symbol,
            description = description,
            price = BigDecimal(price),
            currency = "USD",
            previousClosePrice = BigDecimal(price),
            pricePrecision = pricePrecision,
        )
    }
}
