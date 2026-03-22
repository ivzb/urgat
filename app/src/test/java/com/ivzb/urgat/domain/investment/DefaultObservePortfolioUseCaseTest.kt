package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.forex.FakeForexRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.data.portfolio.Portfolio
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObservePortfolioUseCaseTest {

    private lateinit var fakeObserveInvestmentAssetsUseCase: FakeObserveInvestmentAssetsUseCase
    private lateinit var fakeMarketPriceRepository: FakeMarketPriceRepository
    private lateinit var fakeForexRepository: FakeForexRepository
    private lateinit var useCase: ObservePortfolioUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        fakeObserveInvestmentAssetsUseCase = FakeObserveInvestmentAssetsUseCase()
        fakeMarketPriceRepository = FakeMarketPriceRepository()
        fakeForexRepository = FakeForexRepository()

        useCase = DefaultObservePortfolioUseCase(
            marketPriceRepository = fakeMarketPriceRepository,
            forexRepository = fakeForexRepository,
            observeInvestmentAssetsUseCase = fakeObserveInvestmentAssetsUseCase,
            scope = testScope,
        )
    }

    @Test
    fun `emits loading when assets are loading`() = runTest(testDispatcher) {
        fakeObserveInvestmentAssetsUseCase.emit(Async.Loading)

        val result = useCase(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Loading)
    }

    @Test
    fun `emits error when assets emit error`() = runTest(testDispatcher) {
        fakeObserveInvestmentAssetsUseCase.emit(Async.Error)

        val result = useCase(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Error)
    }

    @Test
    fun `emits success with empty list when assets are empty`() = runTest(testDispatcher) {
        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(emptyList()))

        val result = useCase(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        assertEquals(emptyList<Portfolio>(), (result as Async.Success).data)
    }

    @Test
    fun `emits success with portfolio items when market prices are fetched`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["GOOGL"] = MarketPrice("GOOGL", BigDecimal("180.00"), "USD")
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice("AAPL", BigDecimal("250.00"), "USD")

        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))

        val result = useCase(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val portfolio = (result as Async.Success).data
        assertEquals(2, portfolio.size)

        assertEquals("GOOGL", portfolio[0].name)
        assertEquals("Stocks", portfolio[0].category)
        assertEquals(BigDecimal("10"), portfolio[0].shares)
        assertEquals(BigDecimal("1800.00"), portfolio[0].marketValue)
        assertEquals("USD", portfolio[0].currency)

        assertEquals("AAPL", portfolio[1].name)
        assertEquals("Stocks", portfolio[1].category)
        assertEquals(BigDecimal("5"), portfolio[1].shares)
        assertEquals(BigDecimal("1250.00"), portfolio[1].marketValue)
        assertEquals("USD", portfolio[1].currency)
    }

    @Test
    fun `emits success with zero market value when price fetch fails`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.shouldThrow = true

        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))

        val result = useCase(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val portfolio = (result as Async.Success).data
        assertEquals(2, portfolio.size)

        assertEquals("GOOGL", portfolio[0].name)
        assertEquals(BigDecimal.ZERO, portfolio[0].marketValue)
        assertEquals("USD", portfolio[0].currency)

        assertEquals("AAPL", portfolio[1].name)
        assertEquals(BigDecimal.ZERO, portfolio[1].marketValue)
        assertEquals("USD", portfolio[1].currency)
    }

    @Test
    fun `emits success with fallback when some prices fail`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["GOOGL"] = MarketPrice("GOOGL", BigDecimal("180.00"), "USD")
        fakeMarketPriceRepository.failingSymbols.add("AAPL")

        fakeObserveInvestmentAssetsUseCase.emit(Async.Success(fakeAssets))

        val result = useCase(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ).first()

        advanceUntilIdle()

        assertTrue(result is Async.Success)
        val portfolio = (result as Async.Success).data
        assertEquals(2, portfolio.size)

        assertEquals("GOOGL", portfolio[0].name)
        assertEquals(BigDecimal("1800.00"), portfolio[0].marketValue)
        assertEquals("USD", portfolio[0].currency)

        assertEquals("AAPL", portfolio[1].name)
        assertEquals(BigDecimal.ZERO, portfolio[1].marketValue)
        assertEquals("USD", portfolio[1].currency)
    }

    companion object {

        private val fakeInvestments = listOf(
            Investment("1", "GOOGL", BigDecimal("10"), BigDecimal("150"), "Stocks", "USD", 1L, false, 1L),
            Investment("2", "AAPL", BigDecimal("5"), BigDecimal("200"), "Stocks", "USD", 1L, false, 1L),
        )

        private val fakeAssets = listOf(
            InvestmentAsset(
                id = "1", name = "GOOGL", category = "Stocks",
                shares = BigDecimal("10"), marketValue = BigDecimal("1500"), currency = "USD",
                investments = listOf(fakeInvestments[0]), total = "1,500 USD",
            ),
            InvestmentAsset(
                id = "2", name = "AAPL", category = "Stocks",
                shares = BigDecimal("5"), marketValue = BigDecimal("1000"), currency = "USD",
                investments = listOf(fakeInvestments[1]), total = "1,000 USD",
            ),
        )
    }
}


