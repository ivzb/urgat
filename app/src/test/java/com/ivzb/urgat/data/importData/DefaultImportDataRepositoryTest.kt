package com.ivzb.urgat.data.importData

import androidx.room.withTransaction
import com.ivzb.urgat.data.source.local.AppDatabase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultImportDataRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ImportDataRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        database = mockk()

        // `withTransaction` is a Room extension function that requires a real database,
        // so it is stubbed to simply run the block it is given.
        mockkStatic("androidx.room.RoomDatabaseKt")
        coEvery { database.withTransaction(any<suspend () -> Any?>()) } coAnswers {
            secondArg<suspend () -> Any?>().invoke()
        }

        repository = DefaultImportDataRepository(mockk(), database, testDispatcher)
    }

    @After
    fun tearDown() {
        unmockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun `importString parses every line`() = runTest(testDispatcher) {
        val inserted = mutableListOf<String>()

        repository.importString(
            content = "one\ntwo\nthree",
            parseEntity = { it },
            insertEntities = { inserted += it },
        )

        assertEquals(listOf("one", "two", "three"), inserted)
    }

    @Test
    fun `importString does not insert anything for empty content`() = runTest(testDispatcher) {
        val inserted = mutableListOf<String>()

        repository.importString(
            content = "",
            parseEntity = { it },
            insertEntities = { inserted += it },
        )

        assertEquals(listOf(), inserted)
    }

    @Test
    fun `importString keeps quoted multiline rows as a single entity`() = runTest(testDispatcher) {
        val inserted = mutableListOf<String>()

        repository.importString(
            content = "\"1\", \"first\"\n\"2\", \"second\nline\"\n\"3\", \"third\"",
            parseEntity = { it },
            insertEntities = { inserted += it },
        )

        assertEquals(
            listOf(
                "\"1\", \"first\"",
                "\"2\", \"second\nline\"",
                "\"3\", \"third\"",
            ),
            inserted,
        )
    }

    @Test
    fun `importString inserts entities in batches of 500`() = runTest(testDispatcher) {
        val batchSizes = mutableListOf<Int>()

        repository.importString(
            content = (1..1200).joinToString("\n") { "row $it" },
            parseEntity = { it },
            insertEntities = { batchSizes += it.size },
        )

        assertEquals(listOf(500, 500, 200), batchSizes)
    }

    @Test
    fun `importString applies the parse function to every line`() = runTest(testDispatcher) {
        val inserted = mutableListOf<Int>()

        repository.importString(
            content = "1\n2\n3",
            parseEntity = { it.toInt() },
            insertEntities = { inserted += it },
        )

        assertEquals(listOf(1, 2, 3), inserted)
    }

}

