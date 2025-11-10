package com.ivzb.urgat.data.weight

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultWeightsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeWeightDao
    private lateinit var repository: DefaultWeightsRepository
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeWeightDao()
        repository = DefaultWeightsRepository(
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
    fun `create adds new weight and returns its id`() = runTest {
        val id = repository.create(
            value = 75.toBigDecimal()
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals(75.toBigDecimal(), stored?.value)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct weight`() = runTest {
        val id = repository.create(
            value = 75.toBigDecimal()
        )

        val weight = repository.get(id)
        assertNotNull(weight)
        assertEquals(75.toBigDecimal(), weight?.value)
    }

    @Test
    fun `delete removes weight`() = runTest {
        val id = repository.create(75.toBigDecimal())
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `getStream emits mapped weights`() = runTest {
        repository.create(75.toBigDecimal())
        repository.create(77.toBigDecimal())

        val result = repository.getStream(limit = 10).first()

        assertEquals(2, result.size)
        assertEquals(listOf(75.toBigDecimal(), 77.toBigDecimal()), result.map { it.value })
    }

    @Test
    fun `getStream with searchQuery filters correctly`() = runTest {
        repository.create(75.toBigDecimal())
        repository.create(77.toBigDecimal())
        repository.create(79.toBigDecimal())

        val result = repository.getStream(limit = 10, searchQuery = "Lu").first()

        // Should match "Lunch" only (case-insensitive substring)
        assertEquals(3, result.size)
        assertEquals(listOf(75.toBigDecimal(), 77.toBigDecimal(), 79.toBigDecimal()), result.map { it.value })
    }
}
