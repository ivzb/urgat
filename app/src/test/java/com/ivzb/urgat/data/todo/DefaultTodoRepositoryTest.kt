package com.ivzb.urgat.data.todo

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultTodoRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeTodoDao
    private lateinit var repository: DefaultTodoRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeTodoDao()
        repository = DefaultTodoRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new todo and returns its id`() = runTest {
        val id = repository.create(
            content = "This is a test todo",
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("This is a test todo", stored?.content)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct todo`() = runTest {
        val id = repository.create(
            content = "This is a test todo",
        )

        val todo = repository.get(id)
        assertNotNull(todo)
        assertEquals("This is a test todo", todo?.content)
    }

    @Test
    fun `update modifies existing todo`() = runTest {
        val id = repository.create("Old")

        repository.update(id, "Updated")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("Updated", updated?.content)
    }

    @Test
    fun `update throws when todo does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "A")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes todo`() = runTest {
        val id = repository.create("Temp")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped todo`() = runTest {
        repository.create("A")
        repository.create("B")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("A", "B"), result.map { it.content })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("Taxi")
        repository.create("Lunch")
        repository.create("Bus")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "Ta").first()

        assertEquals(1, result.size)
        assertEquals("Taxi", result[0].content)
    }

    @Test
    fun `complete updates todo correctly`() = runTest {
        val id = repository.create("Taxi")

        var todo = dao.getById(id)
        assertNotNull(todo)
        todo?.let {
            assertFalse(it.isCompleted)
        }

        repository.complete(id)

        todo = dao.getById(id)
        assertNotNull(todo)
        todo?.let {
            assertTrue(it.isCompleted)
        }
    }

    @Test
    fun `activate updates todo correctly`() = runTest {
        val id = repository.create("Taxi")
        var todo = dao.getById(id)

        assertNotNull(todo)
        todo?.let {
            assertFalse(it.isCompleted)
        }

        repository.complete(id)

        assertNotNull(todo)
        todo = dao.getById(id)
        todo?.let {
            assertTrue(it.isCompleted)
        }

        repository.activate(id)

        todo = dao.getById(id)
        assertNotNull(todo)
        todo?.let {
            assertFalse(it.isCompleted)
        }
    }

    @Test
    fun `clearCompleted deletes completed todos`() = runTest {
        val id1 = repository.create("Taxi")
        repository.create("Lunch")
        val id3 = repository.create("Bus")

        repository.complete(id1)
        repository.complete(id3)

        repository.clearCompleted()

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        // Should match "Lunch" only (case-insensitive substring)
        assertEquals(1, result.size)
        assertEquals("Lunch", result.first().content)
    }
}
