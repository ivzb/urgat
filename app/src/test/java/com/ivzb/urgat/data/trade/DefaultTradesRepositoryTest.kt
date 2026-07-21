package com.ivzb.urgat.data.trade

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
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
        amount: BigDecimal = BigDecimal("150"),
        currency: String = "USD",
        date: Long = 1L,
    ) {
        dao.upsert(
            TradeEntity(
                id = id,
                name = name,
                amount = amount,
                currency = currency,
                date = date,
                isDeleted = false,
                createdAt = System.currentTimeMillis(),
            )
        )
    }

    @Test
    fun `get retrieves correct trade`() = runTest {
        insertTrade(id = "1", name = "AAPL", amount = BigDecimal("150"), currency = "USD", date = 1L)

        val trade = repository.get("1")
        assertNotNull(trade)
        assertEquals("AAPL", trade?.name)
        assertEquals(BigDecimal("150"), trade?.amount)
        assertEquals("USD", trade?.currency)
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
        insertTrade(id = "1", name = "TSLA", amount = BigDecimal("300"), currency = "EUR", date = 100L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        val trade = result.first()
        assertEquals("1", trade.id)
        assertEquals("TSLA", trade.name)
        assertEquals(BigDecimal("300"), trade.amount)
        assertEquals("EUR", trade.currency)
        assertEquals(100L, trade.date)
        assertEquals(false, trade.isDeleted)
    }

    @Test
    fun `refresh triggers re-emission`() = runTest {
        insertTrade(id = "1", name = "AAPL")

        val emissions = mutableListOf<List<Trade>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        // Initial load should not have triggered refreshing
        assertEquals(false, repository.isRefreshingFlow.value)

        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        // After refresh completes, isRefreshing should be false
        assertEquals(false, repository.isRefreshingFlow.value)

        job.cancel()
    }

}

