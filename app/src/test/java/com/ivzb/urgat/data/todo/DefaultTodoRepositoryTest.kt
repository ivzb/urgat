package com.ivzb.urgat.data.todo

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
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeTodoDao()
        repository = DefaultTodoRepository(
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
    fun `create adds new todo and returns its id`() = runTest {
        val id = repository.create(
            title = "Test Todo",
            description = "This is a test description",
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("Test Todo", stored?.title)
        assertEquals("This is a test description", stored?.description)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct todo`() = runTest {
        val id = repository.create(
            title = "Test Todo",
            description = "This is a test description",
        )

        val todo = repository.get(id)
        assertNotNull(todo)
        assertEquals("Test Todo", todo?.title)
        assertEquals("This is a test description", todo?.description)
    }

    @Test
    fun `update modifies existing todo`() = runTest {
        val id = repository.create("Old", "Desc")

        repository.update(id, "Updated", "Updated Desc")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("Updated", updated?.title)
        assertEquals("Updated Desc", updated?.description)
    }

    @Test
    fun `update throws when todo does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "A", "B")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes todo`() = runTest {
        val id = repository.create("Temp", "Desc")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `getStream emits mapped todo`() = runTest {
        repository.create("A", "Desc")
        repository.create("B", "Desc")

        val result = repository.getStream(limit = 10).first()

        assertEquals(2, result.size)
        assertEquals(listOf("A", "B"), result.map { it.title })
    }

    @Test
    fun `getStream with searchQuery filters correctly`() = runTest {
        repository.create("Taxi", "Desc")
        repository.create("Lunch", "Tamagochi")
        repository.create("Bus", "Desc")

        val result = repository.getStream(limit = 10, searchQuery = "Ta").first()

        // Should match "Lunch" only (case-insensitive substring)
        assertEquals(2, result.size)
        assertEquals("Taxi", result[0].title)
        assertEquals("Lunch", result[1].title)
    }

    @Test
    fun `complete updates todo correctly`() = runTest {
        val id = repository.create("Taxi", "Desc")

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
        val id = repository.create("Taxi", "Desc")
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
        val id1 = repository.create("Taxi", "Desc")
        val id2 = repository.create("Lunch", "Tamagochi")
        val id3 = repository.create("Bus", "Desc")

        repository.complete(id1)
        repository.complete(id3)

        repository.clearCompleted()

        val result = repository.getStream(limit = 10).first()

        // Should match "Lunch" only (case-insensitive substring)
        assertEquals(1, result.size)
        assertEquals("Lunch", result.first().title)
    }
}
