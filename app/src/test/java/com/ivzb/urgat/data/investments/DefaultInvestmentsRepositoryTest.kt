package com.ivzb.urgat.data.investments

import com.ivzb.urgat.data.investment.DefaultInvestmentsRepository
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
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeInvestmentDao()
        repository = DefaultInvestmentsRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
            scope = scope
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
            currency = "USD",
            amount = BigDecimal("350"),
            category = "etfs",
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("SNP 500", stored?.name)
        assertEquals("USD", stored?.currency)
        assertEquals(BigDecimal("350"), stored?.amount)
        assertEquals("etfs", stored?.category)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct investment`() = runTest {
        val id = repository.create(
            name = "SNP 500",
            currency = "USD",
            amount = BigDecimal("350"),
            category = "etfs",
        )

        val investment = repository.get(id)
        assertNotNull(investment)
        assertEquals("SNP 500", investment?.name)
        assertEquals("USD", investment?.currency)
        assertEquals(BigDecimal("350"), investment?.amount)
        assertEquals("etfs", investment?.category)
    }

    @Test
    fun `update modifies existing investment`() = runTest {
        val id = repository.create("SNP 500", "USD", 300.toBigDecimal(), "etfs")

        repository.update(id, "S&P 500", "USD", 550.toBigDecimal(), "etfs")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("S&P 500", updated?.name)
        assertEquals("USD", updated?.currency)
        assertEquals(BigDecimal("550"), updated?.amount)
        assertEquals("etfs", updated?.category)
    }

    @Test
    fun `update throws when investment does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "SNP 500", "USD", BigDecimal.ONE, "etfs")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes investment`() = runTest {
        val id = repository.create("Temp", "USD", BigDecimal.ONE, "etfs")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `getStream emits mapped investments`() = runTest {
        repository.create("SNP 500", "USD", 300.toBigDecimal(), "etfs")
        repository.create("BRK.B", "USD", 440.toBigDecimal(), "etfs")

        val result = repository.getStream(limit = 10).first()

        assertEquals(2, result.size)
        assertEquals(listOf("SNP 500", "BRK.B"), result.map { it.name })
    }

    @Test
    fun `getStream with searchQuery filters correctly`() = runTest {
        repository.create("SNP 500", "USD", 300.toBigDecimal(), "etfs")
        repository.create("BRK.B", "USD", 440.toBigDecimal(), "etfs")

        val result = repository.getStream(limit = 10, searchQuery = "SNP").first()

        assertEquals(1, result.size)
        assertEquals("SNP 500", result.first().name)
    }
}
