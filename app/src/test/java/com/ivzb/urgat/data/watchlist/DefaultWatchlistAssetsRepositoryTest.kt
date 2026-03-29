package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
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
class DefaultWatchlistAssetsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeWatchlistAssetDao
    private lateinit var repository: DefaultWatchlistAssetsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeWatchlistAssetDao()
        repository = DefaultWatchlistAssetsRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new watchlist asset and returns its id`() = runTest {
        val id = repository.create(
            name = "AAPL",
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("AAPL", stored?.name)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct watchlist asset`() = runTest {
        val id = repository.create(
            name = "AAPL",
        )

        val watchlistAsset = repository.get(id)
        assertNotNull(watchlistAsset)
        assertEquals("AAPL", watchlistAsset?.name)
    }

    @Test
    fun `get returns null for non-existing watchlist asset`() = runTest {
        val watchlistAsset = repository.get("non-existing-id")
        assertNull(watchlistAsset)
    }

    @Test
    fun `update modifies existing watchlist asset`() = runTest {
        val id = repository.create("AAPL")

        repository.update(id, "GOOGL")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("GOOGL", updated?.name)
    }

    @Test
    fun `update throws when watchlist asset does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "GOOGL")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes watchlist asset`() = runTest {
        val id = repository.create("AAPL")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped watchlist assets`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("AAPL", "GOOGL"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        assertEquals("AAPL", result.first().name)
    }
}

