package com.ivzb.urgat.data.forex

import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.util.FakeLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    @Test
    fun `clearCache emits on refreshFlow`() = runTest(testDispatcher) {
        val refreshReceived = async {
            repository.refreshFlow.first()
        }

        advanceUntilIdle()

        repository.clearCache()

        advanceUntilIdle()

        refreshReceived.await()
    }

    @Test
    fun `clearCache causes next getExchangeRate to call api again`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertEquals(1, fakeMarketPriceRepository.fetchCount)

        repository.clearCache()

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertEquals(2, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `clearCache clears all cached currency pairs`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)
        fakeMarketPriceRepository.prices["GBPUSD=X"] = MarketPrice("GBPUSD=X", "GBP/USD", BigDecimal("1.27"), "USD", BigDecimal("1.26"), 4)

        repository.getExchangeRate("EUR", "USD")
        repository.getExchangeRate("GBP", "USD")
        advanceUntilIdle()

        assertEquals(2, fakeMarketPriceRepository.fetchCount)

        repository.clearCache()

        repository.getExchangeRate("EUR", "USD")
        repository.getExchangeRate("GBP", "USD")
        advanceUntilIdle()

        assertEquals(4, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `clearCache allows fetching updated rate`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        val oldRate = repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertEquals(BigDecimal("1.08"), oldRate)

        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.12"), "USD", BigDecimal("1.11"), 4)

        repository.clearCache()

        val newRate = repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertEquals(BigDecimal("1.12"), newRate)
        assertNotEquals(oldRate, newRate)
    }

    @Test
    fun `clearCache also clears cached inverse rates`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] = MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        repository.getExchangeRate("USD", "EUR")
        advanceUntilIdle()

        assertEquals(1, fakeMarketPriceRepository.fetchCount)

        repository.clearCache()

        fakeMarketPriceRepository.prices["USDEUR=X"] = MarketPrice("USDEUR=X", "USD/EUR", BigDecimal("0.90"), "EUR", BigDecimal("0.89"), 4)

        repository.getExchangeRate("USD", "EUR")
        advanceUntilIdle()

        assertEquals(2, fakeMarketPriceRepository.fetchCount)
    }

    @Test
    fun `refreshFlow emits each time clearCache is called`() = runTest(testDispatcher) {
        var emissionCount = 0

        val job = launch {
            repository.refreshFlow.collect {
                emissionCount++
            }
        }

        advanceUntilIdle()

        repository.clearCache()
        advanceUntilIdle()
        assertEquals(1, emissionCount)

        repository.clearCache()
        advanceUntilIdle()
        assertEquals(2, emissionCount)

        repository.clearCache()
        advanceUntilIdle()
        assertEquals(3, emissionCount)

        job.cancel()
    }

    @Test
    fun `does not cache a failed rate`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.failingSymbols += "EURUSD=X"

        assertNull(repository.getExchangeRate("EUR", "USD"))

        fakeMarketPriceRepository.failingSymbols.clear()
        fakeMarketPriceRepository.prices["EURUSD=X"] =
            MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)

        advanceUntilIdle()

        // no clearCache anywhere - the next conversion simply asks again and succeeds
        assertEquals(BigDecimal("1.08"), repository.getExchangeRate("EUR", "USD"))
    }

    @Test
    fun `failed rate does not evict an unrelated cached rate`() = runTest(testDispatcher) {
        fakeMarketPriceRepository.prices["EURUSD=X"] =
            MarketPrice("EURUSD=X", "EUR/USD", BigDecimal("1.08"), "USD", BigDecimal("1.07"), 4)
        fakeMarketPriceRepository.failingSymbols += "GBPUSD=X"

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        assertNull(repository.getExchangeRate("GBP", "USD"))

        val fetchesBefore = fakeMarketPriceRepository.fetchCount

        repository.getExchangeRate("EUR", "USD")
        advanceUntilIdle()

        // the good rate stayed cached, unlike with a blanket clearCache
        assertEquals(fetchesBefore, fakeMarketPriceRepository.fetchCount)
    }

}
