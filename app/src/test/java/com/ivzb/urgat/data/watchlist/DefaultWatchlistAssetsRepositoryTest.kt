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

    @Test
    fun `toExternal maps all entity fields correctly with market data`() = runTest {
        val marketPrice = MarketPrice(
            symbol = "MSFT",
            description = "Microsoft Corporation",
            price = BigDecimal("415.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("412.00"),
            pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["MSFT"] = marketPrice

        val id = repository.create("MSFT")

        val entity = dao.getById(id)
        val asset = repository.get(id)

        assertNotNull(entity)
        assertNotNull(asset)
        assertEquals(entity?.id, asset?.id)
        assertEquals("MSFT", asset?.name)
        assertEquals("Microsoft Corporation", asset?.description)
        assertEquals(BigDecimal("415.50"), asset?.marketPrice)
        assertEquals("USD", asset?.currency)
        assertEquals(BigDecimal("412.00"), asset?.previousClosePrice)
        assertEquals(2, asset?.pricePrecision)
        assertEquals(false, asset?.isDeleted)
        assertEquals(entity?.createdAt, asset?.createdAt)
    }

    @Test
    fun `toExternal maps all fields to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        val id = repository.create("UNKNOWN")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("UNKNOWN", asset?.name)
        assertNull(asset?.description)
        assertNull(asset?.marketPrice)
        assertNull(asset?.currency)
        assertNull(asset?.previousClosePrice)
        assertNull(asset?.pricePrecision)
    }

    @Test
    fun `toExternal preserves entity id through mapping`() = runTest {
        val id = repository.create("TEST")

        val entity = dao.getById(id)
        val asset = repository.get(id)

        assertNotNull(entity)
        assertNotNull(asset)
        assertEquals(entity?.id, asset?.id)
    }

    @Test
    fun `toExternal preserves isDeleted as false for new asset`() = runTest {
        val id = repository.create("TEST")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(false, asset?.isDeleted)
    }

    @Test
    fun `toExternal preserves createdAt timestamp`() = runTest {
        val id = repository.create("TEST")

        val entity = dao.getById(id)
        val asset = repository.get(id)

        assertNotNull(entity)
        assertNotNull(asset)
        assertEquals(entity?.createdAt, asset?.createdAt)
    }

    @Test
    fun `toExternal propagates pricePrecision from market data`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("189.00"),
            pricePrecision = 4,
        )

        val id = repository.create("AAPL")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(4, asset?.pricePrecision)
    }

    @Test
    fun `toExternal sets pricePrecision to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        val id = repository.create("UNKNOWN")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertNull(asset?.pricePrecision)
    }

    @Test
    fun `toExternal with pricePrecision zero`() = runTest {
        fakeMarketPriceRepository.prices["ROUND"] = MarketPrice(
            symbol = "ROUND",
            description = "Rounded Asset",
            price = BigDecimal("100"),
            currency = "JPY",
            previousClosePrice = BigDecimal("99"),
            pricePrecision = 0,
        )

        val id = repository.create("ROUND")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(0, asset?.pricePrecision)
    }

    @Test
    fun `toExternal with high pricePrecision`() = runTest {
        fakeMarketPriceRepository.prices["SHIB"] = MarketPrice(
            symbol = "SHIB",
            description = "Shiba Inu",
            price = BigDecimal("0.00000850"),
            currency = "USD",
            previousClosePrice = BigDecimal("0.00000845"),
            pricePrecision = 8,
        )

        val id = repository.create("SHIB")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(8, asset?.pricePrecision)
        assertEquals(BigDecimal("0.00000850"), asset?.marketPrice)
        assertEquals(BigDecimal("0.00000845"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal maps currency from market data`() = runTest {
        fakeMarketPriceRepository.prices["VUSA.L"] = MarketPrice(
            symbol = "VUSA.L",
            description = "Vanguard S&P 500",
            price = BigDecimal("75.00"),
            currency = "GBP",
            previousClosePrice = BigDecimal("74.50"),
            pricePrecision = 2,
        )

        val id = repository.create("VUSA.L")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("GBP", asset?.currency)
    }

    @Test
    fun `toExternal maps previousClosePrice from market data`() = runTest {
        fakeMarketPriceRepository.prices["TSLA"] = MarketPrice(
            symbol = "TSLA",
            description = "Tesla Inc.",
            price = BigDecimal("250.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("245.75"),
            pricePrecision = 2,
        )

        val id = repository.create("TSLA")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("245.75"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal description comes from market data not entity name`() = runTest {
        fakeMarketPriceRepository.prices["TICKER"] = MarketPrice(
            symbol = "TICKER",
            description = "Full Company Name Inc.",
            price = BigDecimal("100"),
            currency = "USD",
            previousClosePrice = BigDecimal("99"),
            pricePrecision = 2,
        )

        val id = repository.create("TICKER")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("TICKER", asset?.name)
        assertEquals("Full Company Name Inc.", asset?.description)
    }

    @Test
    fun `toExternal handles asset name with special characters`() = runTest {
        fakeMarketPriceRepository.prices["BRK.B"] = MarketPrice(
            symbol = "BRK.B",
            description = "Berkshire Hathaway B",
            price = BigDecimal("400"),
            currency = "USD",
            previousClosePrice = BigDecimal("398"),
            pricePrecision = 2,
        )

        val id = repository.create("BRK.B")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("BRK.B", asset?.name)
        assertEquals("Berkshire Hathaway B", asset?.description)
    }

    @Test
    fun `toExternal after update reflects updated name`() = runTest {
        fakeMarketPriceRepository.prices["GOOGL"] = MarketPrice(
            symbol = "GOOGL",
            description = "Alphabet Inc.",
            price = BigDecimal("175"),
            currency = "USD",
            previousClosePrice = BigDecimal("173"),
            pricePrecision = 2,
        )

        val id = repository.create("AAPL")
        repository.update(id, "GOOGL")

        val asset = repository.get(id)
        assertNotNull(asset)
        assertEquals(id, asset?.id)
        assertEquals("GOOGL", asset?.name)
        assertEquals("Alphabet Inc.", asset?.description)
        assertEquals(BigDecimal("175"), asset?.marketPrice)
    }

    @Test
    fun `toExternal handles very large market price`() = runTest {
        fakeMarketPriceRepository.prices["BRK.A"] = MarketPrice(
            symbol = "BRK.A",
            description = "Berkshire Hathaway A",
            price = BigDecimal("650000.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("649500.00"),
            pricePrecision = 2,
        )

        val id = repository.create("BRK.A")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("650000.00"), asset?.marketPrice)
        assertEquals(BigDecimal("649500.00"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal handles zero market price`() = runTest {
        fakeMarketPriceRepository.prices["DELIST"] = MarketPrice(
            symbol = "DELIST",
            description = "Delisted Stock",
            price = BigDecimal.ZERO,
            currency = "USD",
            previousClosePrice = BigDecimal("5.00"),
            pricePrecision = 2,
        )

        val id = repository.create("DELIST")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal.ZERO, asset?.marketPrice)
        assertEquals(BigDecimal("5.00"), asset?.previousClosePrice)
    }

    @Test
    fun `observe toExternal maps all fields for each asset in list`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple Inc.", price = BigDecimal("190"), currency = "USD", previousClosePrice = BigDecimal("188"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["MSFT"] = MarketPrice(
            symbol = "MSFT", description = "Microsoft Corp.", price = BigDecimal("415"), currency = "USD", previousClosePrice = BigDecimal("412"), pricePrecision = 3,
        )

        repository.create("AAPL")
        repository.create("MSFT")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)

        val apple = result.first { it.name == "AAPL" }
        assertEquals("Apple Inc.", apple.description)
        assertEquals(BigDecimal("190"), apple.marketPrice)
        assertEquals("USD", apple.currency)
        assertEquals(BigDecimal("188"), apple.previousClosePrice)
        assertEquals(2, apple.pricePrecision)
        assertEquals(false, apple.isDeleted)

        val msft = result.first { it.name == "MSFT" }
        assertEquals("Microsoft Corp.", msft.description)
        assertEquals(BigDecimal("415"), msft.marketPrice)
        assertEquals("USD", msft.currency)
        assertEquals(BigDecimal("412"), msft.previousClosePrice)
        assertEquals(3, msft.pricePrecision)
        assertEquals(false, msft.isDeleted)
    }

    @Test
    fun `observe toExternal handles mixed market data availability`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple Inc.", price = BigDecimal("190"), currency = "USD", previousClosePrice = BigDecimal("188"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.failingSymbols.add("MSFT")

        repository.create("AAPL")
        repository.create("MSFT")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)

        val apple = result.first { it.name == "AAPL" }
        assertEquals("Apple Inc.", apple.description)
        assertEquals(BigDecimal("190"), apple.marketPrice)
        assertEquals("USD", apple.currency)
        assertEquals(BigDecimal("188"), apple.previousClosePrice)
        assertEquals(2, apple.pricePrecision)

        val msft = result.first { it.name == "MSFT" }
        assertNull(msft.description)
        assertNull(msft.marketPrice)
        assertNull(msft.currency)
        assertNull(msft.previousClosePrice)
        assertNull(msft.pricePrecision)
    }

    @Test
    fun `observe toExternal empty list returns empty`() = runTest {
        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `observe toExternal preserves order from dao`() = runTest {
        repository.create("C")
        repository.create("A")
        repository.create("B")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(3, result.size)
        assertEquals(listOf("C", "A", "B"), result.map { it.name })
    }

    @Test
    fun `observe toExternal with searchQuery maps all market fields`() = runTest {
        fakeMarketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL", description = "Apple Inc.", price = BigDecimal("190"), currency = "USD", previousClosePrice = BigDecimal("188"), pricePrecision = 2,
        )

        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        val asset = result.first()
        assertEquals("AAPL", asset.name)
        assertEquals("Apple Inc.", asset.description)
        assertEquals(BigDecimal("190"), asset.marketPrice)
        assertEquals("USD", asset.currency)
        assertEquals(BigDecimal("188"), asset.previousClosePrice)
        assertEquals(2, asset.pricePrecision)
        assertEquals(false, asset.isDeleted)
    }

    @Test
    fun `toExternal maps different currencies from market data`() = runTest {
        fakeMarketPriceRepository.prices["USD_ASSET"] = MarketPrice(
            symbol = "USD_ASSET", description = "USD Asset", price = BigDecimal("100"), currency = "USD", previousClosePrice = BigDecimal("99"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["EUR_ASSET"] = MarketPrice(
            symbol = "EUR_ASSET", description = "EUR Asset", price = BigDecimal("200"), currency = "EUR", previousClosePrice = BigDecimal("198"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["GBP_ASSET"] = MarketPrice(
            symbol = "GBP_ASSET", description = "GBP Asset", price = BigDecimal("300"), currency = "GBP", previousClosePrice = BigDecimal("297"), pricePrecision = 2,
        )

        repository.create("USD_ASSET")
        repository.create("EUR_ASSET")
        repository.create("GBP_ASSET")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals("USD", result.first { it.name == "USD_ASSET" }.currency)
        assertEquals("EUR", result.first { it.name == "EUR_ASSET" }.currency)
        assertEquals("GBP", result.first { it.name == "GBP_ASSET" }.currency)
    }

    @Test
    fun `toExternal maps equal price and previousClosePrice`() = runTest {
        fakeMarketPriceRepository.prices["FLAT"] = MarketPrice(
            symbol = "FLAT",
            description = "Flat Stock",
            price = BigDecimal("100.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("100.00"),
            pricePrecision = 2,
        )

        val id = repository.create("FLAT")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("100.00"), asset?.marketPrice)
        assertEquals(BigDecimal("100.00"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal maps market price higher than previous close`() = runTest {
        fakeMarketPriceRepository.prices["UP"] = MarketPrice(
            symbol = "UP",
            description = "Going Up",
            price = BigDecimal("110.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("100.00"),
            pricePrecision = 2,
        )

        val id = repository.create("UP")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("110.00"), asset?.marketPrice)
        assertEquals(BigDecimal("100.00"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal maps market price lower than previous close`() = runTest {
        fakeMarketPriceRepository.prices["DOWN"] = MarketPrice(
            symbol = "DOWN",
            description = "Going Down",
            price = BigDecimal("90.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("100.00"),
            pricePrecision = 2,
        )

        val id = repository.create("DOWN")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("90.00"), asset?.marketPrice)
        assertEquals(BigDecimal("100.00"), asset?.previousClosePrice)
    }

    @Test
    fun `get returns null for non-existing asset after mapping`() = runTest {
        val asset = repository.get("does-not-exist")
        assertNull(asset)
    }
}
