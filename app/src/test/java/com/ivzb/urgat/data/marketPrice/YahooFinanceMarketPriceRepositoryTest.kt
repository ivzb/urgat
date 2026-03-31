package com.ivzb.urgat.data.marketPrice

import com.ivzb.urgat.util.FakeLogger
import com.ivzb.urgat.util.round
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

        coVerify { api.fetch("GOOGL") }
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
                        )
                    ),
                    YahooFinanceResponse.Result(
                        meta = YahooFinanceResponse.Meta(
                            regularMarketPrice = 200.0,
                            currency = "EUR",
                            shortName = "Apple Inc. (AAPL)",
                            chartPreviousClose = 199.50,
                        )
                    ),
                )
            )
        )

        coEvery { api.fetch("AAPL") } returns response

        val result = requireNotNull(repository.fetchPrice("AAPL"))

        assertEquals(BigDecimal("100"), result.price)
        assertEquals("USD", result.currency)
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
                            currency = "USD",
                            shortName = "",
                            chartPreviousClose = 0.0,
                        )
                    )
                )
            )
        )

        coEvery { api.fetch("DELISTED") } returns response

        val result = requireNotNull(repository.fetchPrice("DELISTED"))

        assertEquals("DELISTED", result.symbol)
        assertEquals(BigDecimal("0"), result.price)
        assertEquals("USD", result.currency)
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
}

