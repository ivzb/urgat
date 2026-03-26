package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.investmentMarketPrice.FakeInvestmentMarketPriceRepository
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

    private val fakeMarketPriceRepository = FakeInvestmentMarketPriceRepository()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeInvestmentDao()
        repository = DefaultInvestmentsRepository(
            localDataSource = dao,
            investmentMarketPriceRepository = fakeMarketPriceRepository,
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
}
