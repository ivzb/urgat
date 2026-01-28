package com.ivzb.urgat.data.saving

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
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
class DefaultSavingsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeSavingDao
    private lateinit var repository: DefaultSavingsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeSavingDao()
        repository = DefaultSavingsRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new saving and returns its id`() = runTest {
        val id = repository.create(
            name = "Emergency fund",
            amount = BigDecimal("500"),
            date = 1000L
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("Emergency fund", stored?.name)
        assertEquals(BigDecimal("500"), stored?.amount)
        assertEquals(1000L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct saving`() = runTest {
        val id = repository.create(
            name = "Emergency fund",
            amount = BigDecimal("500"),
            date = 1000L
        )

        val saving = repository.get(id)
        assertNotNull(saving)
        assertEquals("Emergency fund", saving?.name)
        assertEquals(BigDecimal("500"), saving?.amount)
        assertEquals(1000L, saving?.date)
        assertTrue(saving?.isDeleted == false)
    }

    @Test
    fun `update modifies existing saving`() = runTest {
        val id = repository.create("Old", BigDecimal.ONE, 3000L)

        repository.update(id, "Updated", BigDecimal.TEN, 4000L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("Updated", updated?.name)
        assertEquals(BigDecimal.TEN, updated?.amount)
        assertEquals(4000L, updated?.date)
    }

    @Test
    fun `update throws when saving does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "A", BigDecimal.ONE, 0L)
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes saving`() = runTest {
        val id = repository.create("Temp", BigDecimal.ONE, 0L)
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped savings`() = runTest {
        repository.create("A", BigDecimal("1.0"), 0L)
        repository.create("B", BigDecimal("2.0"), 0L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("A", "B"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("Taxi", BigDecimal("10.0"), 0L)
        repository.create("Lunch", BigDecimal("8.0"), 0L)
        repository.create("Bus", BigDecimal("3.0"), 0L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "Lu").first()

        // Should match "Lunch" only (case-insensitive substring)
        assertEquals(1, result.size)
        assertEquals("Lunch", result.first().name)
    }
}
