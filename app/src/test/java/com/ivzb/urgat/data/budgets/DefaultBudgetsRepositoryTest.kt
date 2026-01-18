package com.ivzb.urgat.data.budgets

import com.ivzb.urgat.data.budget.DefaultBudgetsRepository
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
class DefaultBudgetsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeBudgetDao
    private lateinit var repository: DefaultBudgetsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeBudgetDao()
        repository = DefaultBudgetsRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new budget and returns its id`() = runTest {
        val id = repository.create(
            income = BigDecimal("6500"),
            bankStart = BigDecimal("11000"),
            bankEnd = BigDecimal("8800"),
            date = 1L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals(BigDecimal("6500"), stored?.income)
        assertEquals(BigDecimal("11000"), stored?.bankStart)
        assertEquals(BigDecimal("8800"), stored?.bankEnd)
        assertEquals(1L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `create adds new budget without bankEnd and returns its id`() = runTest {
        val id = repository.create(
            income = BigDecimal("6500"),
            bankStart = BigDecimal("11000"),
            bankEnd = null,
            date = 1L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals(BigDecimal("6500"), stored?.income)
        assertEquals(BigDecimal("11000"), stored?.bankStart)
        assertEquals(null, stored?.bankEnd)
        assertEquals(1L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct budget`() = runTest {
        val id = repository.create(
            income = BigDecimal("6500"),
            bankStart = BigDecimal("11000"),
            bankEnd = BigDecimal("8800"),
            date = 1L,
        )

        val budget = repository.get(id)
        assertNotNull(budget)
        assertEquals(BigDecimal("6500"), budget?.income)
        assertEquals(BigDecimal("11000"), budget?.bankStart)
        assertEquals(BigDecimal("8800"), budget?.bankEnd)
        assertEquals(1L, budget?.date)
    }

    @Test
    fun `update modifies existing budget`() = runTest {
        val id = repository.create(6500.toBigDecimal(), 11000.toBigDecimal(), 8800.toBigDecimal(), 1L)

        repository.update(id, 6300.toBigDecimal(), 11200.toBigDecimal(), 9000.toBigDecimal(), 2L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals(BigDecimal("6300"), updated?.income)
        assertEquals(BigDecimal("11200"), updated?.bankStart)
        assertEquals(BigDecimal("9000"), updated?.bankEnd)
        assertEquals(2L, updated?.date)
    }

    @Test
    fun `update throws when budget does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", 6300.toBigDecimal(), 11200.toBigDecimal(), 9000.toBigDecimal(), 2L)
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes budget`() = runTest {
        val id = repository.create(6500.toBigDecimal(), 11000.toBigDecimal(), 8800.toBigDecimal(), 1L)
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped budgets`() = runTest {
        repository.create(6500.toBigDecimal(), 11000.toBigDecimal(), 8800.toBigDecimal(), 1L)
        repository.create(6000.toBigDecimal(), 10000.toBigDecimal(), 8000.toBigDecimal(), 2L)

        val result = repository.observe(limit = 10).first()

        assertEquals(2, result.size)
        assertEquals(listOf("6500".toBigDecimal(), "6000".toBigDecimal()), result.map { it.income })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create(6500.toBigDecimal(), 11000.toBigDecimal(), 8800.toBigDecimal(), 1L)
        repository.create(6000.toBigDecimal(), 10000.toBigDecimal(), 8000.toBigDecimal(), 2L)

        val result = repository.observe(limit = 10, searchQuery = "search is not supported").first()

        assertEquals(2, result.size)
        assertEquals(listOf("6500".toBigDecimal(), "6000".toBigDecimal()), result.map { it.income })
    }
}
