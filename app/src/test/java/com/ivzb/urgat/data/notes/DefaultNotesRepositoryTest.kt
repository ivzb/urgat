package com.ivzb.urgat.data.notes

import com.ivzb.urgat.data.note.DefaultNotesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultNotesRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeNoteDao
    private lateinit var repository: DefaultNotesRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeNoteDao()
        repository = DefaultNotesRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new note and returns its id`() = runTest {
        val id = repository.create(
            content = "Note"
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("Note", stored?.content)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct note`() = runTest {
        val id = repository.create(
            content = "Note"
        )

        val note = repository.get(id)
        assertNotNull(note)
        assertEquals("Note", note?.content)
    }

    @Test
    fun `update modifies existing note`() = runTest {
        val id = repository.create("Old")

        repository.update(id, "Updated")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("Updated", updated?.content)
    }

    @Test
    fun `update throws when note does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "A")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes note`() = runTest {
        val id = repository.create("Temp")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped notes`() = runTest {
        repository.create("A")
        repository.create("B")

        val result = repository.observe(limit = 10).first()

        assertEquals(2, result.size)
        assertEquals(listOf("A", "B"), result.map { it.content })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("Taxi")
        repository.create("Lunch")
        repository.create("Bus")

        val result = repository.observe(limit = 10, searchQuery = "Lu").first()

        assertEquals(1, result.size)
        assertEquals("Lunch", result.first().content)
    }
}
