package com.ivzb.urgat.data.forex

import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.util.FakeLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultForexRepositoryTest {

    private lateinit var fakeMarketPriceRepository: FakeMarketPriceRepository
    private lateinit var repository: DefaultForexRepository

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        fakeMarketPriceRepository = FakeMarketPriceRepository()

        repository = DefaultForexRepository(
            marketPriceRepository = fakeMarketPriceRepository,
            logger = FakeLogger(),
            scope = testScope,
        )
    }

    @Test
    fun `same currency returns one`() = runTest(testDispatcher) {
        val rate = repository.getExchangeRate("USD", "USD")

        assertEquals(BigDecimal.ONE, rate)
    }

    @Test
    fun `same currency case insensitive returns one`() = runTest(testDispatcher) {
        val rate = repository.getExchangeRate("usd", "USD")

        assertEquals(BigDecimal.ONE, rate)
    }

    @Test
    fun `fetches exchange rate from market price repository`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        val rate = repository.getExchangeRate("EUR", "USD")

        advanceUntilIdle()

        assertEquals(BigDecimal("1.08"), rate)
    }

    @Test
    fun `convert multiplies amount by exchange rate`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        val result = repository.convert(BigDecimal("100"), "EUR", "USD")

        advanceUntilIdle()

        assertEquals(BigDecimal("108.00"), result)
    }

    @Test
    fun `caches exchange rate after first fetch`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertEquals(1, fakeMarketPriceRepository.fetchCount)

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertEquals(1, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `caches inverse rate after fetch`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        val inverseRate = repository.getExchangeRate("USD", "EUR")
        advanceUntilIdle()

        val expectedInverse = BigDecimal("0.9259259259")
        assertEquals(expectedInverse, inverseRate)
        assertEquals(1, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `parallel requests for same pair result in single fetch`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        val results = (1..10).map {
            async { repository.getExchangeRate("EUR", "USD") }
        }.awaitAll()

        advanceUntilIdle()

        results.forEach { rate ->
            assertEquals(BigDecimal("1.08"), rate)
        }

        assertEquals(1, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `different currency pairs are fetched independently`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)
        fakeMarketPriceRepository.prices["GBPUSD=X"] = MarketPrice("GBPUSD=X", "GBP/USD", BigDecimal("1.27"), "USD", BigDecimal("1.26"), 4)

        val eurUsd = repository.getExchangeRate("EUR", "USD")
        val gbpUsd = repository.getExchangeRate("GBP", "USD")

        advanceUntilIdle()

        assertEquals(BigDecimal("1.08"), eurUsd)
        assertEquals(BigDecimal("1.27"), gbpUsd)
        assertEquals(2, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `throws when market price fetch fails`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.shouldThrow = true

        val rate = repository.getExchangeRate("EUR", "USD")

        advanceUntilIdle()

        assertNull(rate)
    }

    @Test
    fun `handles lowercase currency codes`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD",BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        val rate = repository.getExchangeRate("eur", "usd")

        advanceUntilIdle()

        assertEquals(BigDecimal("1.08"), rate)
    }

}
