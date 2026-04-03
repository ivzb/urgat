package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultWatchlistAssetsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeWatchlistAssetDao
    private lateinit var repository: DefaultWatchlistAssetsRepository

    private val fakeMarketPriceRepository = FakeMarketPriceRepository()

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

    @Test
    fun `create adds new watchlist asset and returns its id`() = runTest {
        val id = repository.create(
            name = "AAPL",
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("AAPL", stored?.name)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct watchlist asset`() = runTest {
        val id = repository.create(
            name = "AAPL",
        )

        val watchlistAsset = repository.get(id)
        assertNotNull(watchlistAsset)
        assertEquals("AAPL", watchlistAsset?.name)
    }

    @Test
    fun `get returns null for non-existing watchlist asset`() = runTest {
        val watchlistAsset = repository.get("non-existing-id")
        assertNull(watchlistAsset)
    }

    @Test
    fun `update modifies existing watchlist asset`() = runTest {
        val id = repository.create("AAPL")

        repository.update(id, "GOOGL")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("GOOGL", updated?.name)
    }

    @Test
    fun `update throws when watchlist asset does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "GOOGL")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes watchlist asset`() = runTest {
        val id = repository.create("AAPL")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped watchlist assets`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("AAPL", "GOOGL"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        assertEquals("AAPL", result.first().name)
    }

    @Test
    fun `observe populates market price from fetchPrice`() = runTest {
        val marketPrice = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("150.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("148.00"),
            pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["AAPL"] = marketPrice

        repository.create("AAPL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        val asset = result.first()
        assertEquals("Apple Inc.", asset.description)
        assertEquals(BigDecimal("150.00"), asset.marketPrice)
        assertEquals("USD", asset.currency)
        assertEquals(BigDecimal("148.00"), asset.previousClosePrice)
        assertEquals(2, asset.pricePrecision)
    }

    @Test
    fun `observe returns null market data when fetchPrice returns null`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        repository.create("AAPL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        val asset = result.first()
        assertEquals("AAPL", asset.name)
        assertNull(asset.description)
        assertNull(asset.marketPrice)
        assertNull(asset.currency)
        assertNull(asset.previousClosePrice)
    }

    @Test
    fun `observe calls fetchPrice for each asset`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["GOOGL"] = MarketPrice(
            symbol = "GOOGL", description = "Google", price = BigDecimal("2800"), currency = "USD", previousClosePrice = BigDecimal("2790"), pricePrecision = 2,
        )

        repository.create("AAPL")
        repository.create("GOOGL")

        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
        assertEquals("Apple", result.find { it.name == "AAPL" }?.description)
        assertEquals("Google", result.find { it.name == "GOOGL" }?.description)
    }

    @Test
    fun `observe with searchQuery populates market price`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple Inc.", price = BigDecimal("150.00"), currency = "USD", previousClosePrice = BigDecimal("148.00"), pricePrecision = 2,
        )

        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        assertEquals("Apple Inc.", result.first().description)
        assertEquals(BigDecimal("150.00"), result.first().marketPrice)
        assertEquals(2, result.first().pricePrecision)
    }

    @Test
    fun `observe handles partial market price availability`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.failingSymbols.add("GOOGL")

        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        val apple = result.first { it.name == "AAPL" }
        val google = result.first { it.name == "GOOGL" }
        assertEquals("Apple", apple.description)
        assertEquals(BigDecimal("150"), apple.marketPrice)
        assertNull(google.description)
        assertNull(google.marketPrice)
    }

    @Test
    fun `get populates market price from fetchPrice`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple Inc.", price = BigDecimal("150.00"), currency = "USD", previousClosePrice = BigDecimal("148.00"), pricePrecision = 2,
        )

        val id = repository.create("AAPL")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("Apple Inc.", asset?.description)
        assertEquals(BigDecimal("150.00"), asset?.marketPrice)
        assertEquals("USD", asset?.currency)
        assertEquals(BigDecimal("148.00"), asset?.previousClosePrice)
    }

    @Test
    fun `get returns null market data when fetchPrice returns null`() = runTest {
        val id = repository.create("AAPL")

        fakeMarketPriceRepository.shouldThrow = true
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("AAPL", asset?.name)
        assertNull(asset?.description)
        assertNull(asset?.marketPrice)
        assertNull(asset?.currency)
        assertNull(asset?.previousClosePrice)
    }

    @Test
    fun `get calls fetchPrice exactly once`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )

        val id = repository.create("AAPL")
        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        repository.get(id)

        assertEquals(1, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
    }

    @Test
    fun `isRefreshingFlow starts as false`() = runTest {
        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe emits`() = runTest {
        repository.create("AAPL")

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe with searchQuery emits`() = runTest {
        repository.create("AAPL")

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `refresh triggers re-emission with updated market data`() = runTest {
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        // First emission: no market price set
        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().marketPrice)

        // Now set market price and trigger refresh
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        // Second emission should have market data
        assertTrue(emissions.size >= 2)
        val refreshedAsset = emissions.last().first()
        assertEquals("Apple", refreshedAsset.description)
        assertEquals(BigDecimal("150"), refreshedAsset.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh triggers re-emission for observe with searchQuery`() = runTest {
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().marketPrice)

        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple Inc.", price = BigDecimal("155"), currency = "USD", previousClosePrice = BigDecimal("150"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        val refreshedAsset = emissions.last().first()
        assertEquals("Apple Inc.", refreshedAsset.description)
        assertEquals(BigDecimal("155"), refreshedAsset.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh re-fetches prices for all observed assets`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        val fetchCountBeforeRefresh = fakeMarketPriceRepository.fetchCount

        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["GOOGL"] = MarketPrice(
            symbol = "GOOGL", description = "Google", price = BigDecimal("2800"), currency = "USD", previousClosePrice = BigDecimal("2790"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        // Should have fetched price for each asset during refresh
        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBeforeRefresh)

        val lastEmission = emissions.last()
        assertEquals("Apple", lastEmission.find { it.name == "AAPL" }?.description)
        assertEquals("Google", lastEmission.find { it.name == "GOOGL" }?.description)

        job.cancel()
    }

    @Test
    fun `multiple refreshes each trigger new emissions`() = runTest {
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        val emissionsAfterInitial = emissions.size

        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterInitial)
        val emissionsAfterFirstRefresh = emissions.size

        // Update price and refresh again
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("160"), currency = "USD", previousClosePrice = BigDecimal("150"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterFirstRefresh)
        assertEquals(BigDecimal("160"), emissions.last().first().marketPrice)

        job.cancel()
    }

    @Test
    fun `isRefreshingFlow resets to false after refresh completes`() = runTest {
        repository.create("AAPL")

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {}
        }

        advanceUntilIdle()
        assertEquals(false, repository.isRefreshingFlow.value)

        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertEquals(false, repository.isRefreshingFlow.value)

        job.cancel()
    }

    @Test
    fun `clearCache clears market data and triggers re-emission`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple", price = BigDecimal("150"), currency = "USD", previousClosePrice = BigDecimal("148"), pricePrecision = 2,
        )
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        // First emission has market data
        assertTrue(emissions.isNotEmpty())
        assertEquals(BigDecimal("150"), emissions.first().first().marketPrice)

        // clearCache clears prices, so after refresh market data should be null
        fakeMarketPriceRepository.clearCache()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        assertNull(emissions.last().first().marketPrice)

        job.cancel()
    }
}

