package com.ivzb.urgat.data.trade

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultTradesRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeTradeDao
    private lateinit var repository: DefaultTradesRepository

    private val fakeMarketPriceRepository = FakeMarketPriceRepository()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeTradeDao()
        repository = DefaultTradesRepository(
            localDataSource = dao,
            marketPriceRepository = fakeMarketPriceRepository,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun insertTrade(
        id: String = "1",
        name: String = "AAPL",
        shares: BigDecimal = BigDecimal("10"),
        fee: BigDecimal = BigDecimal("1.5"),
        marketPrice: BigDecimal = BigDecimal("150"),
        currency: String = "USD",
        orderType: String = "buy",
        date: Long = 1L,
    ) {
        dao.upsert(
            TradeEntity(
                id = id,
                name = name,
                shares = shares,
                fee = fee,
                marketPrice = marketPrice,
                currency = currency,
                orderType = orderType,
                date = date,
                isDeleted = false,
                createdAt = System.currentTimeMillis(),
            )
        )
    }

    @Test
    fun `get retrieves correct trade`() = runTest {
        insertTrade(id = "1", name = "AAPL", shares = BigDecimal("10"), fee = BigDecimal("1.5"), marketPrice = BigDecimal("150"), currency = "USD", orderType = "buy", date = 1L)

        val trade = repository.get("1")
        assertNotNull(trade)
        assertEquals("AAPL", trade?.name)
        assertEquals(BigDecimal("10"), trade?.shares)
        assertEquals(BigDecimal("1.5"), trade?.fee)
        assertEquals(BigDecimal("150"), trade?.marketPrice)
        assertEquals("USD", trade?.currency)
        assertEquals("buy", trade?.orderType)
        assertEquals(1L, trade?.date)
        assertEquals(false, trade?.isDeleted)
    }

    @Test
    fun `get returns null for non-existent trade`() = runTest {
        val trade = repository.get("missing")
        assertNull(trade)
    }

    @Test
    fun `delete removes trade`() = runTest {
        insertTrade(id = "1")
        assertNotNull(dao.getById("1"))

        repository.delete("1")
        assertNull(dao.getById("1"))
    }

    @Test
    fun `observe emits mapped trades`() = runTest {
        insertTrade(id = "1", name = "AAPL")
        insertTrade(id = "2", name = "GOOG")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("AAPL", "GOOG"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        insertTrade(id = "1", name = "AAPL")
        insertTrade(id = "2", name = "GOOG")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        assertEquals("AAPL", result.first().name)
    }

    @Test
    fun `observe populates pricePrecision from fetchPrice`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("189.00"),
            pricePrecision = 4,
        )

        insertTrade(id = "1", name = "AAPL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(4, result.first().pricePrecision)
    }

    @Test
    fun `observe sets pricePrecision to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        insertTrade(id = "1", name = "UNKNOWN")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertNull(result.first().pricePrecision)
    }

    @Test
    fun `get sets pricePrecision to null always`() = runTest {
        insertTrade(id = "1", name = "AAPL")

        val trade = repository.get("1")
        assertNotNull(trade)
        assertNull(trade?.pricePrecision)
    }

    @Test
    fun `isRefreshingFlow starts as false`() = runTest {
        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow stays false on initial observe`() = runTest {
        insertTrade(id = "1")

        val refreshStates = mutableListOf<Boolean>()
        val refreshJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.isRefreshingFlow.collect { refreshStates.add(it) }
        }

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {}
        }

        advanceUntilIdle()

        // isRefreshing should never have been true during initial load
        assertTrue(refreshStates.all { !it })

        job.cancel()
        refreshJob.cancel()
    }

    @Test
    fun `isRefreshingFlow becomes true then false on actual refresh`() = runTest {
        insertTrade(id = "1")

        val refreshStates = mutableListOf<Boolean>()
        val refreshJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.isRefreshingFlow.collect { refreshStates.add(it) }
        }

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {}
        }

        advanceUntilIdle()
        refreshStates.clear()

        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        // Should have been true at some point, then false
        assertTrue(refreshStates.contains(true))
        assertEquals(false, refreshStates.last())

        job.cancel()
        refreshJob.cancel()
    }

    @Test
    fun `isRefreshingFlow becomes false after observe emits`() = runTest {
        insertTrade(id = "1")

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow stays false on observe with searchQuery initial load`() = runTest {
        insertTrade(id = "1")

        val refreshStates = mutableListOf<Boolean>()
        val refreshJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.isRefreshingFlow.collect { refreshStates.add(it) }
        }

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").collect {}
        }

        advanceUntilIdle()

        assertTrue(refreshStates.all { !it })

        job.cancel()
        refreshJob.cancel()
    }

    @Test
    fun `observe empty list returns empty`() = runTest {
        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `observe preserves trade fields`() = runTest {
        insertTrade(id = "1", name = "TSLA", shares = BigDecimal("5"), fee = BigDecimal("2.0"), marketPrice = BigDecimal("300"), currency = "EUR", orderType = "sell", date = 100L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        val trade = result.first()
        assertEquals("1", trade.id)
        assertEquals("TSLA", trade.name)
        assertEquals(BigDecimal("5"), trade.shares)
        assertEquals(BigDecimal("2.0"), trade.fee)
        assertEquals(BigDecimal("300"), trade.marketPrice)
        assertEquals("EUR", trade.currency)
        assertEquals("sell", trade.orderType)
        assertEquals(100L, trade.date)
        assertEquals(false, trade.isDeleted)
    }

    @Test
    fun `refresh triggers re-emission with updated market data`() = runTest {
        insertTrade(id = "1", name = "AAPL")

        val emissions = mutableListOf<List<Trade>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().pricePrecision)
        // Initial load should not have triggered refreshing
        assertEquals(false, repository.isRefreshingFlow.value)

        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("190"), currency = "USD", previousClosePrice = BigDecimal("189"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        assertEquals(2, emissions.last().first().pricePrecision)
        // After refresh completes, isRefreshing should be false
        assertEquals(false, repository.isRefreshingFlow.value)

        job.cancel()
    }

}

