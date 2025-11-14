package com.ivzb.urgat.data.expenses

import com.ivzb.urgat.data.expense.DefaultExpensesRepository
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
class DefaultExpensesRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeExpenseDao
    private lateinit var repository: DefaultExpensesRepository
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeExpenseDao()
        repository = DefaultExpensesRepository(
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
    fun `create adds new expense and returns its id`() = runTest {
        val id = repository.create(
            name = "Coffee",
            amount = BigDecimal("3.50"),
            category = "Food",
            date = 1000L
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("Coffee", stored?.name)
        assertEquals(BigDecimal("3.50"), stored?.amount)
        assertEquals(1000L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct expense`() = runTest {
        val id = repository.create(
            name = "Lunch",
            amount = BigDecimal("10.0"),
            category = "Food",
            date = 2000L
        )

        val expense = repository.get(id)
        assertNotNull(expense)
        assertEquals("Lunch", expense?.name)
        assertEquals(BigDecimal("10.0"), expense?.amount)
        assertEquals(2000L, expense?.date)
    }

    @Test
    fun `update modifies existing expense`() = runTest {
        val id = repository.create("Old", BigDecimal.ONE, "Misc", 3000L)

        repository.update(id, "Updated", BigDecimal.TEN, "Travel", 4000L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("Updated", updated?.name)
        assertEquals(BigDecimal.TEN, updated?.amount)
        assertEquals("Travel", updated?.category)
    }

    @Test
    fun `update throws when expense does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "A", BigDecimal.ONE, "Cat", 0L)
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes expense`() = runTest {
        val id = repository.create("Temp", BigDecimal.ONE, "Misc", 0L)
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `getStream emits mapped expenses`() = runTest {
        repository.create("A", BigDecimal("1.0"), "Cat", 0L)
        repository.create("B", BigDecimal("2.0"), "Cat", 0L)

        val result = repository.getStream(limit = 10).first()

        assertEquals(2, result.size)
        assertEquals(listOf("A", "B"), result.map { it.name })
    }

    @Test
    fun `getStream with searchQuery filters correctly`() = runTest {
        repository.create("Taxi", BigDecimal("10.0"), "Transport", 0L)
        repository.create("Lunch", BigDecimal("8.0"), "Food", 0L)
        repository.create("Bus", BigDecimal("3.0"), "Transport", 0L)

        val result = repository.getStream(limit = 10, searchQuery = "Lu").first()

        // Should match "Lunch" only (case-insensitive substring)
        assertEquals(1, result.size)
        assertEquals("Lunch", result.first().name)
    }
}
