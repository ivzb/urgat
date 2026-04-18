package com.ivzb.urgat.data.carMaintenance

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
class DefaultCarMaintenanceRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeCarMaintenanceDao
    private lateinit var repository: DefaultCarMaintenanceRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeCarMaintenanceDao()
        repository = DefaultCarMaintenanceRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new car maintenance and returns its id`() = runTest {
        val id = repository.create(
            description = "Oil change",
            partNumber = "ABC123",
            url = "https://example.com",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("Oil change", stored?.description)
        assertEquals("ABC123", stored?.partNumber)
        assertEquals("https://example.com", stored?.url)
        assertEquals("Mercedes w140", stored?.carName)
        assertEquals(150000, stored?.odometer)
        assertEquals(1L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `create with null url stores null`() = runTest {
        val id = repository.create(
            description = "Brake pads",
            partNumber = "XYZ789",
            url = null,
            carName = "BMW E39",
            odometer = 200000,
            date = 2L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertNull(stored?.url)
    }

    @Test
    fun `get retrieves correct car maintenance`() = runTest {
        val id = repository.create(
            description = "Oil change",
            partNumber = "ABC123",
            url = null,
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1L,
        )

        val carMaintenance = repository.get(id)
        assertNotNull(carMaintenance)
        assertEquals("Oil change", carMaintenance?.description)
        assertEquals("ABC123", carMaintenance?.partNumber)
        assertEquals("Mercedes w140", carMaintenance?.carName)
        assertEquals(150000, carMaintenance?.odometer)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest {
        val carMaintenance = repository.get("non-existent")
        assertNull(carMaintenance)
    }

    @Test
    fun `update modifies existing car maintenance`() = runTest {
        val id = repository.create("Oil change", "ABC123", null, "Mercedes w140", 150000, 1L)

        repository.update(id, "Brake pads", "XYZ789", "https://example.com", "BMW E39", 160000, 2L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("Brake pads", updated?.description)
        assertEquals("XYZ789", updated?.partNumber)
        assertEquals("https://example.com", updated?.url)
        assertEquals("BMW E39", updated?.carName)
        assertEquals(160000, updated?.odometer)
        assertEquals(2L, updated?.date)
    }

    @Test
    fun `update throws when car maintenance does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "desc", "part", null, "Car", 100000, 2L)
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes car maintenance`() = runTest {
        val id = repository.create("Oil change", "ABC123", null, "Mercedes w140", 150000, 1L)
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped car maintenance`() = runTest {
        repository.create("Oil change", "P1", null, "Car B", 149500, 1L)
        repository.create("Brake pads", "P2", null, "Car A", 150000, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("Brake pads", "Oil change"), result.map { it.description })
    }

    @Test
    fun `observe with searchQuery filters results`() = runTest {
        repository.create("Oil change", "P1", null, "Car B", 149500, 1L)
        repository.create("Brake pads", "P2", null, "Car A", 150000, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "Oil").first()

        assertEquals(1, result.size)
        assertEquals("Oil change", result[0].description)
    }

    @Test
    fun `observe respects limit`() = runTest {
        repository.create("Oil change", "P1", null, "Car A", 150000, 1L)
        repository.create("Brake pads", "P2", null, "Car B", 149500, 2L)
        repository.create("Air filter", "P3", null, "Car C", 149000, 3L)

        val result = repository.observe(limit = 2, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
    }

    @Test
    fun `observe respects date range`() = runTest {
        repository.create("Oil change", "P1", null, "Car A", 150000, 100L)
        repository.create("Brake pads", "P2", null, "Car B", 149500, 200L)
        repository.create("Air filter", "P3", null, "Car C", 149000, 300L)

        val result = repository.observe(limit = 10, dateRange = LongRange(150L, 250L)).first()

        assertEquals(1, result.size)
        assertEquals("Brake pads", result[0].description)
    }

    @Test
    fun `getLastUsedCarName returns null when no car maintenance exists`() = runTest {
        assertNull(repository.getLastUsedCarName())
    }

    @Test
    fun `getLastUsedCarName returns carName from most recently created car maintenance`() = runTest {
        repository.create("Oil change", "P1", null, "Car A", 150000, 1L)
        repository.create("Brake pads", "P2", null, "Car B", 149500, 2L)

        assertEquals("Car B", repository.getLastUsedCarName())
    }
}

