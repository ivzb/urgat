package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.util.FakeLogger
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.round
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class YahooFinanceMarketPriceRepositoryTest {

    private lateinit var api: YahooFinanceApi
    private lateinit var repository: YahooFinanceMarketPriceRepository

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        api = mockk()
        repository = YahooFinanceMarketPriceRepository(
            dispatcher = testDispatcher,
            api = api,
            logger = FakeLogger(),
            scope = testScope,
        )
    }

    @Test
    fun `fetchPrice returns correct market price`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 180.50,
                            currency = "USD",
                            shortName = "Alphabet Inc. (GOOGL)",
                            chartPreviousClose = 179.80,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("GOOGL") } returns response

        val result = requireNotNull(repository.fetchPrice("GOOGL"))

        assertEquals("GOOGL", result.symbol)
        assertEquals(BigDecimal("180.5"), result.price)
        assertEquals("USD", result.currency)
        assertEquals(2, result.pricePrecision)

        coVerify { api.fetch("GOOGL") }
    }

    @Test
    fun `fetchPrice normalizes symbol and returns correct market price`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 4_581.70,
                            currency = "USD",
                            shortName = "Gold Jun 26 (GC=F)",
                            chartPreviousClose = 4_557.50,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("GC=F") } returns response

        val result = requireNotNull(repository.fetchPrice("XAU"))

        assertEquals("GC=F", result.symbol)
        assertEquals(BigDecimal("4581.69").format(), result.price.format())
        assertEquals("USD", result.currency)
        assertEquals(2, result.pricePrecision)

        coVerify { api.fetch("GC=F") }
    }

    @Test
    fun `fetchPrice normalizes currency symbol and returns correct market price`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 1.1469,
                            currency = "USD",
                            shortName = "EUR/USD (EURUSD=X)",
                            chartPreviousClose = 1.1466,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("EURUSD=X") } returns response

        val result = requireNotNull(repository.fetchPrice("€$=X"))

        assertEquals("EURUSD=X", result.symbol)
        assertEquals(BigDecimal("1.1469").format(), result.price.format())
        assertEquals("USD", result.currency)
        assertEquals(2, result.pricePrecision)

        coVerify { api.fetch("EURUSD=X") }
    }

    @Test
    fun `fetchPrice returns correct currency`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 95.30,
                            currency = "EUR",
                            shortName = "Alibaba Group Holding Limited (BABA)",
                            chartPreviousClose = 96.13,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("BABA") } returns response

        val result = requireNotNull(repository.fetchPrice("BABA"))

        assertEquals("BABA", result.symbol)
        assertEquals(BigDecimal("95.30"), result.price.round())
        assertEquals("EUR", result.currency)
        assertEquals(2, result.pricePrecision)
    }

    @Test
    fun `fetchPrice uses first result from chart`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 100.0,
                            currency = "USD",
                            shortName = "Apple Inc. (AAPL)",
                            chartPreviousClose = 99.50,
                            priceHint = 2,
                        )
                    ),
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 200.0,
                            currency = "EUR",
                            shortName = "Apple Inc. (AAPL)",
                            chartPreviousClose = 199.50,
                            priceHint = 2,
                        )
                    ),
                )
            )
        )

        coEvery { api.fetch("AAPL") } returns response

        val result = requireNotNull(repository.fetchPrice("AAPL"))

        assertEquals(BigDecimal("100"), result.price)
        assertEquals("USD", result.currency)
        assertEquals(2, result.pricePrecision)
    }

    @Test
    fun `fetchPrice returns null on api exception`() = runTest(testDispatcher) {
        coEvery { api.fetch("INVALID") } throws RuntimeException("Network error")

        val result = repository.fetchPrice("INVALID")

        assertNull(result)
    }

    @Test
    fun `fetchPrice handles zero price`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 0.0,
                            currency = "",
                            shortName = "",
                            chartPreviousClose = 0.0,
                            priceHint = 0,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("DELISTED") } returns response

        val result = requireNotNull(repository.fetchPrice("DELISTED"))

        assertEquals("DELISTED", result.symbol)
        assertEquals(BigDecimal("0"), result.price)
        assertEquals("", result.currency)
        assertEquals(0, result.pricePrecision)
    }

    @Test
    fun `fetchPrice caches result and calls api only once per symbol`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 150.0,
                            currency = "USD",
                            shortName = "Microsoft Corporation (MSFT)",
                            chartPreviousClose = 149.70,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("MSFT") } returns response

        val first = repository.fetchPrice("MSFT")
        val second = repository.fetchPrice("MSFT")

        assertEquals(first, second)
        coVerify(exactly = 1) { api.fetch("MSFT") }
    }

    @Test
    fun `fetchPrice handles large price values`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 91_234.56,
                            currency = "USD",
                            shortName = "Bitcoin USD",
                            chartPreviousClose = 92_100.33,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("BTC-USD") } returns response

        val result = requireNotNull(repository.fetchPrice("BTC-USD"))

        assertEquals("BTC-USD", result.symbol)
        assertEquals(BigDecimal("91234.56"), result.price.round())
        assertEquals("USD", result.currency)
    }

    @Test
    fun `lastFetchedAt is set once a price is fetched`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 150.0,
                            currency = "USD",
                            shortName = "Microsoft Corporation (MSFT)",
                            chartPreviousClose = 149.70,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("MSFT") } returns response

        assertNull(repository.lastFetchedAt)

        val fetchedAfter = System.currentTimeMillis()

        repository.fetchPrice("MSFT")

        val lastFetchedAt = requireNotNull(repository.lastFetchedAt)

        assertTrue(lastFetchedAt >= fetchedAfter)
    }

    @Test
    fun `lastFetchedAt is left alone when the fetch fails`() = runTest(testDispatcher) {
        coEvery { api.fetch("MSFT") } throws RuntimeException("no connection")

        repository.fetchPrice("MSFT")

        assertNull(repository.lastFetchedAt)
    }

    @Test
    fun `clearCache emits on refreshFlow`() = runTest(testDispatcher) {
        val refreshReceived = async {
            repository.refreshFlow.first()
        }

        advanceUntilIdle()

        repository.clearCache()

        advanceUntilIdle()

        refreshReceived.await() // completes successfully means emission was received
    }

    @Test
    fun `clearCache causes next fetchPrice to call api again`() = runTest(testDispatcher) {
        val response = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 150.0,
                            currency = "USD",
                            shortName = "Microsoft Corporation (MSFT)",
                            chartPreviousClose = 149.70,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("MSFT") } returns response

        val first = repository.fetchPrice("MSFT")
        coVerify(exactly = 1) { api.fetch("MSFT") }

        repository.clearCache()

        val second = repository.fetchPrice("MSFT")
        coVerify(exactly = 2) { api.fetch("MSFT") }

        assertEquals(first, second)
    }

    @Test
    fun `clearCache clears all cached symbols`() = runTest(testDispatcher) {
        val responseAAPL = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 100.0,
                            currency = "USD",
                            shortName = "Apple Inc. (AAPL)",
                            chartPreviousClose = 99.50,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        val responseMSFT = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 150.0,
                            currency = "USD",
                            shortName = "Microsoft Corporation (MSFT)",
                            chartPreviousClose = 149.70,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("AAPL") } returns responseAAPL
        coEvery { api.fetch("MSFT") } returns responseMSFT

        repository.fetchPrice("AAPL")
        repository.fetchPrice("MSFT")
        coVerify(exactly = 1) { api.fetch("AAPL") }
        coVerify(exactly = 1) { api.fetch("MSFT") }

        repository.clearCache()

        repository.fetchPrice("AAPL")
        repository.fetchPrice("MSFT")
        coVerify(exactly = 2) { api.fetch("AAPL") }
        coVerify(exactly = 2) { api.fetch("MSFT") }
    }

    @Test
    fun `clearCache allows fetching updated price`() = runTest(testDispatcher) {
        val oldResponse = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 100.0,
                            currency = "USD",
                            shortName = "Apple Inc. (AAPL)",
                            chartPreviousClose = 99.50,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        val newResponse = YahooFinanceResponse(
            chart = YahooFinanceResponse.Chart(
                result = listOf(
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 105.0,
                            currency = "USD",
                            shortName = "Apple Inc. (AAPL)",
                            chartPreviousClose = 100.0,
                            priceHint = 2,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("AAPL") } returns oldResponse

        val oldResult = requireNotNull(repository.fetchPrice("AAPL"))
        assertEquals(BigDecimal("100"), oldResult.price)

        coEvery { api.fetch("AAPL") } returns newResponse

        repository.clearCache()

        val newResult = requireNotNull(repository.fetchPrice("AAPL"))
        assertEquals(BigDecimal("105"), newResult.price)
        assertNotEquals(oldResult.price, newResult.price)
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
}

