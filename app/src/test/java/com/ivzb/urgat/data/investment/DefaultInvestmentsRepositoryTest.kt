package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.investmentMarketPrice.FakeInvestmentMarketPriceRepository
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
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultInvestmentsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeInvestmentDao
    private lateinit var repository: DefaultInvestmentsRepository

    private val fakeMarketPriceRepository = FakeMarketPriceRepository()
    private val fakeInvestmentMarketPriceRepository = FakeInvestmentMarketPriceRepository()

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

    @Test
    fun `create adds new investment and returns its id`() = runTest {
        val id = repository.create(
            name = "SNP 500",
            shares = BigDecimal("350"),
            costPrice = BigDecimal("200"),
            category = "etfs",
            currency = "USD",
            date = 1L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("SNP 500", stored?.name)
        assertEquals(BigDecimal("350"), stored?.shares)
        assertEquals(BigDecimal("200"), stored?.cost)
        assertEquals("etfs", stored?.category)
        assertEquals("USD", stored?.currency)
        assertEquals(1L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct investment`() = runTest {
        val id = repository.create(
            name = "SNP 500",
            shares = BigDecimal("350"),
            costPrice = BigDecimal("200"),
            category = "etfs",
            currency = "USD",
            date = 1L,
        )

        val investment = repository.get(id)
        assertNotNull(investment)
        assertEquals("SNP 500", investment?.name)
        assertEquals(BigDecimal("350"), investment?.shares)
        assertEquals(BigDecimal("200"), investment?.costPrice)
        assertEquals("etfs", investment?.category)
        assertEquals("USD", investment?.currency)
        assertEquals(1L, investment?.date)
    }

    @Test
    fun `update modifies existing investment`() = runTest {
        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1L)

        repository.update(id, "S&P 500", 550.toBigDecimal(), 250.toBigDecimal(), "etfs", "EUR", 2L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("S&P 500", updated?.name)
        assertEquals(BigDecimal("550"), updated?.shares)
        assertEquals(BigDecimal("250"), updated?.costPrice)
        assertEquals("etfs", updated?.category)
        assertEquals("EUR", updated?.currency)
        assertEquals(2L, updated?.date)
    }

    @Test
    fun `update throws when investment does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "S&P 500", 550.toBigDecimal(), 250.toBigDecimal(), "etfs", "EUR", 2)
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes investment`() = runTest {
        val id = repository.create("Temp", BigDecimal.ONE, BigDecimal.ONE, "etfs", "USD", 1)
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped investments`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("SNP 500", "BRK.B"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").first()

        assertEquals(1, result.size)
        assertEquals("SNP 500", result.first().name)
    }

    @Test
    fun `observe populates description from fetchPrice`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500",
            description = "S&P 500 ETF",
            price = BigDecimal("450.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("448.00"),
            pricePrecision = 2,
        )

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals("S&P 500 ETF", result.first().description)
    }

    @Test
    fun `observe returns null description when fetchPrice returns null`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertNull(result.first().description)
    }

    @Test
    fun `observe calls fetchPrice for each investment`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["BRK.B"] = MarketPrice(
            symbol = "BRK.B", description = "Berkshire", price = BigDecimal("380"), currency = "USD", previousClosePrice = BigDecimal("375"), pricePrecision = 2,
        )

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
        assertEquals("S&P 500", result.find { it.name == "SNP 500" }?.description)
        assertEquals("Berkshire", result.find { it.name == "BRK.B" }?.description)
    }

    @Test
    fun `observe populates marketPrice from calculateMarketPrice`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450.00")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(BigDecimal("450.00"), result.first().marketPrice)
    }

    @Test
    fun `observe falls back to costPrice when calculateMarketPrice returns zero`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(BigDecimal.ZERO, result.first().marketPrice)
    }

    @Test
    fun `observe with searchQuery populates description and marketPrice`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500 ETF", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450.00")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").first()

        assertEquals(1, result.size)
        assertEquals("S&P 500 ETF", result.first().description)
        assertEquals(BigDecimal("450.00"), result.first().marketPrice)
    }

    @Test
    fun `observe handles partial fetchPrice availability`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.failingSymbols.add("BRK.B")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        val snp = result.first { it.name == "SNP 500" }
        val brk = result.first { it.name == "BRK.B" }
        assertEquals("S&P 500", snp.description)
        assertNull(brk.description)
    }

    @Test
    fun `observe handles partial calculateMarketPrice availability`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        val snp = result.first { it.name == "SNP 500" }
        val brk = result.first { it.name == "BRK.B" }
        assertEquals(BigDecimal("450"), snp.marketPrice)
        assertEquals(BigDecimal.ZERO, brk.marketPrice)
    }

    @Test
    fun `get populates description from fetchPrice`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500",
            description = "S&P 500 ETF",
            price = BigDecimal("450.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("448.00"),
            pricePrecision = 2,
        )

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("S&P 500 ETF", investment?.description)
    }

    @Test
    fun `get returns null description when fetchPrice returns null`() = runTest {
        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        fakeMarketPriceRepository.shouldThrow = true
        val investment = repository.get(id)

        assertNotNull(investment)
        assertNull(investment?.description)
    }

    @Test
    fun `get populates marketPrice from calculateMarketPrice`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450.00")

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal("450.00"), investment?.marketPrice)
    }

    @Test
    fun `get falls back to costPrice when calculateMarketPrice returns null`() = runTest {
        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal.ZERO, investment?.marketPrice)
    }

    @Test
    fun `get calls fetchPrice exactly once`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        repository.get(id)

        assertEquals(1, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
    }

    @Test
    fun `get populates both description and marketPrice together`() = runTest {
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500 ETF", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("460.00")

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("S&P 500 ETF", investment?.description)
        assertEquals(BigDecimal("460.00"), investment?.marketPrice)
    }

    @Test
    fun `isRefreshingFlow starts as false`() = runTest {
        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe emits`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe with searchQuery emits`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `refresh triggers re-emission with updated market data`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().description)

        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        val refreshedInvestment = emissions.last().first()
        assertEquals("S&P 500", refreshedInvestment.description)
        assertEquals(BigDecimal("450"), refreshedInvestment.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh triggers re-emission for observe with searchQuery`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().description)

        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500 ETF", price = BigDecimal("455"), currency = "USD", previousClosePrice = BigDecimal("450"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("455")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        val refreshedInvestment = emissions.last().first()
        assertEquals("S&P 500 ETF", refreshedInvestment.description)
        assertEquals(BigDecimal("455"), refreshedInvestment.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh re-fetches prices for all observed investments`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        val fetchCountBeforeRefresh = fakeMarketPriceRepository.fetchCount

        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["BRK.B"] = MarketPrice(
            symbol = "BRK.B", description = "Berkshire", price = BigDecimal("380"), currency = "USD", previousClosePrice = BigDecimal("375"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBeforeRefresh)

        val lastEmission = emissions.last()
        assertEquals("S&P 500", lastEmission.find { it.name == "SNP 500" }?.description)
        assertEquals("Berkshire", lastEmission.find { it.name == "BRK.B" }?.description)

        job.cancel()
    }

    @Test
    fun `multiple refreshes each trigger new emissions`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        val emissionsAfterInitial = emissions.size

        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterInitial)
        val emissionsAfterFirstRefresh = emissions.size

        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("460"), currency = "USD", previousClosePrice = BigDecimal("450"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("460")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterFirstRefresh)
        assertEquals(BigDecimal("460"), emissions.last().first().marketPrice)

        job.cancel()
    }

    @Test
    fun `isRefreshingFlow resets to false after refresh completes`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

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
        fakeMarketPriceRepository.prices["SNP 500"] = MarketPrice(
            symbol = "SNP 500", description = "S&P 500", price = BigDecimal("450"), currency = "USD", previousClosePrice = BigDecimal("448"), pricePrecision = 2,
        )
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertEquals("S&P 500", emissions.first().first().description)
        assertEquals(BigDecimal("450"), emissions.first().first().marketPrice)

        fakeMarketPriceRepository.clearCache()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        assertNull(emissions.last().first().description)

        job.cancel()
    }

    @Test
    fun `toExternal maps entity cost field to costPrice`() = runTest {
        val id = repository.create("TEST", BigDecimal("100"), BigDecimal("55.75"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal("55.75"), investment?.costPrice)
    }

    @Test
    fun `toExternal maps all entity fields correctly`() = runTest {
        val id = repository.create(
            name = "VUSA.L",
            shares = BigDecimal("123.456"),
            costPrice = BigDecimal("78.90"),
            category = "stocks",
            currency = "GBP",
            date = 1700000000L,
        )

        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(id, investment?.id)
        assertEquals("VUSA.L", investment?.name)
        assertEquals(BigDecimal("123.456"), investment?.shares)
        assertEquals(BigDecimal("78.90"), investment?.costPrice)
        assertEquals("stocks", investment?.category)
        assertEquals("GBP", investment?.currency)
        assertEquals(1700000000L, investment?.date)
        assertEquals(false, investment?.isDeleted)
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

        val id = repository.create("AAPL", BigDecimal("10"), BigDecimal("150"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(4, investment?.pricePrecision)
    }

    @Test
    fun `toExternal sets pricePrecision to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        val id = repository.create("UNKNOWN", BigDecimal("10"), BigDecimal("100"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertNull(investment?.pricePrecision)
    }

    @Test
    fun `toExternal uses calculateMarketPrice result as marketPrice`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["TSLA"] = BigDecimal("250.00")

        val id = repository.create("TSLA", BigDecimal("5"), BigDecimal("200"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal("250.00"), investment?.marketPrice)
    }

    @Test
    fun `toExternal falls back to zero marketPrice when calculateMarketPrice returns zero`() = runTest {
        val id = repository.create("NOPRICE", BigDecimal("10"), BigDecimal("100"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal.ZERO, investment?.marketPrice)
    }

    @Test
    fun `toExternal preserves isDeleted as false for new investment`() = runTest {
        val id = repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(false, investment?.isDeleted)
    }

    @Test
    fun `toExternal preserves createdAt timestamp`() = runTest {
        val id = repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)

        val entity = dao.getById(id)
        val investment = repository.get(id)

        assertNotNull(entity)
        assertNotNull(investment)
        assertEquals(entity?.createdAt, investment?.createdAt)
    }

    @Test
    fun `toExternal handles investment name with special characters`() = runTest {
        val id = repository.create("BRK.B", BigDecimal("10"), BigDecimal("400"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("BRK.B", investment?.name)
    }

    @Test
    fun `toExternal handles zero shares`() = runTest {
        val id = repository.create("TEST", BigDecimal.ZERO, BigDecimal("100"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal.ZERO, investment?.shares)
    }

    @Test
    fun `toExternal handles very large shares and cost values`() = runTest {
        val largeShares = BigDecimal("9999999999.999999")
        val largeCost = BigDecimal("99999.99")

        val id = repository.create("BIG", largeShares, largeCost, "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(largeShares, investment?.shares)
        assertEquals(largeCost, investment?.costPrice)
    }

    @Test
    fun `toExternal handles very small fractional cost`() = runTest {
        val smallCost = BigDecimal("0.0001")

        val id = repository.create("MICRO", BigDecimal("1000000"), smallCost, "crypto", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(smallCost, investment?.costPrice)
    }

    @Test
    fun `toExternal maps different categories correctly`() = runTest {
        val idEtf = repository.create("ETF1", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val idStock = repository.create("STK1", BigDecimal("1"), BigDecimal("1"), "stocks", "USD", 2L)
        val idCrypto = repository.create("CRY1", BigDecimal("1"), BigDecimal("1"), "crypto", "USD", 3L)

        assertEquals("etfs", repository.get(idEtf)?.category)
        assertEquals("stocks", repository.get(idStock)?.category)
        assertEquals("crypto", repository.get(idCrypto)?.category)
    }

    @Test
    fun `toExternal maps different currencies correctly`() = runTest {
        val idUsd = repository.create("A", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val idEur = repository.create("B", BigDecimal("1"), BigDecimal("1"), "etfs", "EUR", 2L)
        val idGbp = repository.create("C", BigDecimal("1"), BigDecimal("1"), "etfs", "GBP", 3L)

        assertEquals("USD", repository.get(idUsd)?.currency)
        assertEquals("EUR", repository.get(idEur)?.currency)
        assertEquals("GBP", repository.get(idGbp)?.currency)
    }

    @Test
    fun `toExternal sets description to null when market data unavailable`() = runTest {
        val id = repository.create("NODATA", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertNull(investment?.description)
    }

    @Test
    fun `observe toExternal maps all fields for each investment in list`() = runTest {
        fakeMarketPriceRepository.prices["A"] = MarketPrice(
            symbol = "A", description = "Alpha", price = BigDecimal("100"), currency = "USD", previousClosePrice = BigDecimal("99"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.prices["B"] = MarketPrice(
            symbol = "B", description = "Beta", price = BigDecimal("200"), currency = "EUR", previousClosePrice = BigDecimal("198"), pricePrecision = 3,
        )
        fakeInvestmentMarketPriceRepository.prices["A"] = BigDecimal("105")
        fakeInvestmentMarketPriceRepository.prices["B"] = BigDecimal("210")

        repository.create("A", BigDecimal("10"), BigDecimal("90"), "stocks", "USD", 1L)
        repository.create("B", BigDecimal("20"), BigDecimal("180"), "etfs", "EUR", 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)

        val a = result.first { it.name == "A" }
        assertEquals("Alpha", a.description)
        assertEquals(BigDecimal("105"), a.marketPrice)
        assertEquals(2, a.pricePrecision)
        assertEquals(BigDecimal("10"), a.shares)
        assertEquals(BigDecimal("90"), a.costPrice)
        assertEquals("stocks", a.category)
        assertEquals("USD", a.currency)
        assertEquals(1L, a.date)
        assertEquals(false, a.isDeleted)

        val b = result.first { it.name == "B" }
        assertEquals("Beta", b.description)
        assertEquals(BigDecimal("210"), b.marketPrice)
        assertEquals(3, b.pricePrecision)
        assertEquals(BigDecimal("20"), b.shares)
        assertEquals(BigDecimal("180"), b.costPrice)
        assertEquals("etfs", b.category)
        assertEquals("EUR", b.currency)
        assertEquals(2L, b.date)
        assertEquals(false, b.isDeleted)
    }

    @Test
    fun `observe toExternal handles mixed market data availability`() = runTest {
        fakeMarketPriceRepository.prices["A"] = MarketPrice(
            symbol = "A", description = "Alpha", price = BigDecimal("100"), currency = "USD", previousClosePrice = BigDecimal("99"), pricePrecision = 2,
        )
        fakeMarketPriceRepository.failingSymbols.add("B")
        fakeInvestmentMarketPriceRepository.prices["A"] = BigDecimal("105")

        repository.create("A", BigDecimal("10"), BigDecimal("90"), "stocks", "USD", 1L)
        repository.create("B", BigDecimal("20"), BigDecimal("180"), "etfs", "EUR", 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        val a = result.first { it.name == "A" }
        assertEquals("Alpha", a.description)
        assertEquals(BigDecimal("105"), a.marketPrice)
        assertEquals(2, a.pricePrecision)

        val b = result.first { it.name == "B" }
        assertNull(b.description)
        assertEquals(BigDecimal.ZERO, b.marketPrice)
        assertNull(b.pricePrecision)
    }

    @Test
    fun `toExternal preserves entity id through mapping`() = runTest {
        val id = repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)

        val entity = dao.getById(id)
        val investment = repository.get(id)

        assertNotNull(entity)
        assertNotNull(investment)
        assertEquals(entity?.id, investment?.id)
    }

    @Test
    fun `toExternal after update reflects updated fields`() = runTest {
        val id = repository.create("OLD", BigDecimal("10"), BigDecimal("100"), "etfs", "USD", 1L)

        repository.update(id, "NEW", BigDecimal("20"), BigDecimal("200"), "stocks", "EUR", 2L)

        val investment = repository.get(id)
        assertNotNull(investment)
        assertEquals(id, investment?.id)
        assertEquals("NEW", investment?.name)
        assertEquals(BigDecimal("20"), investment?.shares)
        assertEquals(BigDecimal("200"), investment?.costPrice)
        assertEquals("stocks", investment?.category)
        assertEquals("EUR", investment?.currency)
        assertEquals(2L, investment?.date)
    }

    @Test
    fun `observe toExternal with date zero`() = runTest {
        repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 0L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(0L, result.first().date)
    }

    @Test
    fun `observe toExternal preserves order from dao`() = runTest {
        repository.create("C", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 3L)
        repository.create("A", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        repository.create("B", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(3, result.size)
        assertEquals(listOf("C", "A", "B"), result.map { it.name })
    }

    @Test
    fun `toExternal with pricePrecision zero`() = runTest {
        fakeMarketPriceRepository.prices["ROUND"] = MarketPrice(
            symbol = "ROUND",
            description = "Rounded",
            price = BigDecimal("100"),
            currency = "USD",
            previousClosePrice = BigDecimal("99"),
            pricePrecision = 0,
        )

        val id = repository.create("ROUND", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(0, investment?.pricePrecision)
    }

    @Test
    fun `toExternal with high pricePrecision`() = runTest {
        fakeMarketPriceRepository.prices["PRECISE"] = MarketPrice(
            symbol = "PRECISE",
            description = "High Precision",
            price = BigDecimal("0.00000001"),
            currency = "USD",
            previousClosePrice = BigDecimal("0.00000001"),
            pricePrecision = 8,
        )

        val id = repository.create("PRECISE", BigDecimal("1000000"), BigDecimal("0.00000001"), "crypto", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(8, investment?.pricePrecision)
        assertEquals("High Precision", investment?.description)
    }

    @Test
    fun `toExternal description comes from market data not entity`() = runTest {
        fakeMarketPriceRepository.prices["TICKER"] = MarketPrice(
            symbol = "TICKER",
            description = "Full Company Name",
            price = BigDecimal("100"),
            currency = "USD",
            previousClosePrice = BigDecimal("99"),
            pricePrecision = 2,
        )

        val id = repository.create("TICKER", BigDecimal("1"), BigDecimal("50"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("TICKER", investment?.name)
        assertEquals("Full Company Name", investment?.description)
    }

    @Test
    fun `observe toExternal empty list returns empty`() = runTest {
        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertTrue(result.isEmpty())
    }
}
