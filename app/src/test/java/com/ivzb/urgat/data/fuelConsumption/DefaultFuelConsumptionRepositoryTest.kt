package com.ivzb.urgat.data.fuelConsumption

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
class DefaultFuelConsumptionRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeFuelConsumptionDao
    private lateinit var repository: DefaultFuelConsumptionRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeFuelConsumptionDao()
        repository = DefaultFuelConsumptionRepository(
            localDataSource = dao,
            dispatcher = dispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `create adds new fuel consumption and returns its id`() = runTest {
        val id = repository.create(
            amount = BigDecimal("45.5"),
            price = BigDecimal("2.35"),
            currency = "EUR",
            type = "Diesel",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals(BigDecimal("45.5"), stored?.amount)
        assertEquals(BigDecimal("2.35"), stored?.price)
        assertEquals("EUR", stored?.currency)
        assertEquals("Diesel", stored?.type)
        assertEquals("Mercedes w140", stored?.carName)
        assertEquals(150000, stored?.odometer)
        assertEquals(1L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct fuel consumption`() = runTest {
        val id = repository.create(
            amount = BigDecimal("45.5"),
            price = BigDecimal("2.35"),
            currency = "EUR",
            type = "Diesel",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1L,
        )

        val fuelConsumption = repository.get(id)
        assertNotNull(fuelConsumption)
        assertEquals(BigDecimal("45.5"), fuelConsumption?.amount)
        assertEquals(BigDecimal("2.35"), fuelConsumption?.price)
        assertEquals("EUR", fuelConsumption?.currency)
        assertEquals("Diesel", fuelConsumption?.type)
        assertEquals("Mercedes w140", fuelConsumption?.carName)
        assertEquals(150000, fuelConsumption?.odometer)
        assertEquals(1L, fuelConsumption?.date)
    }

    @Test
    fun `get returns null for non-existent id`() = runTest {
        val fuelConsumption = repository.get("non-existent")
        assertNull(fuelConsumption)
    }

    @Test
    fun `update modifies existing fuel consumption`() = runTest {
        val id = repository.create(
            BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Mercedes w140", 150000, 1L
        )

        repository.update(id, BigDecimal("50.0"), BigDecimal("2.40"), "USD", "Gasoline", "BMW E39", 160000, 2L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals(BigDecimal("50.0"), updated?.amount)
        assertEquals(BigDecimal("2.40"), updated?.price)
        assertEquals("USD", updated?.currency)
        assertEquals("Gasoline", updated?.type)
        assertEquals("BMW E39", updated?.carName)
        assertEquals(160000, updated?.odometer)
        assertEquals(2L, updated?.date)
    }

    @Test
    fun `update throws when fuel consumption does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update(
                "missing", BigDecimal("50.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car", 100000, 2L
            )
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes fuel consumption`() = runTest {
        val id = repository.create(
            BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Mercedes w140", 150000, 1L
        )
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped fuel consumption`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car B", 149500, 1L)
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf(BigDecimal("45.5"), BigDecimal("40.0")), result.map { it.amount })
    }

    @Test
    fun `observe with searchQuery delegates to observe`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car B", 149500, 1L)
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "Car").first()

        assertEquals(2, result.size)
        assertEquals(listOf(BigDecimal("45.5"), BigDecimal("40.0")), result.map { it.amount })
    }

    @Test
    fun `observe respects limit`() = runTest {
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 1L)
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car B", 149500, 2L)
        repository.create(BigDecimal("50.0"), BigDecimal("2.30"), "EUR", "Diesel", "Car C", 149000, 3L)

        val result = repository.observe(limit = 2, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
    }

    @Test
    fun `observe respects date range`() = runTest {
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 100L)
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car B", 149500, 200L)
        repository.create(BigDecimal("50.0"), BigDecimal("2.30"), "EUR", "Diesel", "Car C", 149000, 300L)

        val result = repository.observe(limit = 10, dateRange = LongRange(150L, 250L)).first()

        assertEquals(1, result.size)
        assertEquals(BigDecimal("40.0"), result[0].amount)
    }

    @Test
    fun `getLastUsedCurrency returns null when no fuel consumption exists`() = runTest {
        assertNull(repository.getLastUsedCurrency())
    }

    @Test
    fun `getLastUsedCurrency returns currency from most recently created fuel consumption`() = runTest {
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 1L)
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "USD", "Gasoline", "Car B", 149500, 2L)

        assertEquals("USD", repository.getLastUsedCurrency())
    }

    @Test
    fun `getLastUsedType returns null when no fuel consumption exists`() = runTest {
        assertNull(repository.getLastUsedType())
    }

    @Test
    fun `getLastUsedType returns type from most recently created fuel consumption`() = runTest {
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 1L)
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Gasoline", "Car B", 149500, 2L)

        assertEquals("Gasoline", repository.getLastUsedType())
    }

    @Test
    fun `getLastUsedCarName returns null when no fuel consumption exists`() = runTest {
        assertNull(repository.getLastUsedCarName())
    }

    @Test
    fun `getLastUsedCarName returns carName from most recently created fuel consumption`() = runTest {
        repository.create(BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 1L)
        repository.create(BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car B", 149500, 2L)

        assertEquals("Car B", repository.getLastUsedCarName())
    }

    @Test
    fun `observe calculates distance between same car and type entries`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100500, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        val higher = result.first { it.odometer == 100500 }
        assertEquals(500, higher.distance)

        val lower = result.first { it.odometer == 100000 }
        assertNull(lower.distance)
    }

    @Test
    fun `observe returns null distance when no previous entry exists for same car and type`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertNull(result[0].distance)
    }

    @Test
    fun `observe does not calculate distance across different car names`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car B", 100500, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        result.forEach { assertNull(it.distance) }
    }

    @Test
    fun `observe does not calculate distance across different fuel types`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Gasoline", "Car A", 100500, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        result.forEach { assertNull(it.distance) }
    }

    @Test
    fun `observe calculates distance correctly with multiple cars`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100500, 2L)
        repository.create(BigDecimal("30.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car B", 50000, 3L)
        repository.create(BigDecimal("35.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car B", 50800, 4L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        val carAHigher = result.first { it.carName == "Car A" && it.odometer == 100500 }
        assertEquals(500, carAHigher.distance)

        val carBHigher = result.first { it.carName == "Car B" && it.odometer == 50800 }
        assertEquals(800, carBHigher.distance)

        val carALower = result.first { it.carName == "Car A" && it.odometer == 100000 }
        assertNull(carALower.distance)

        val carBLower = result.first { it.carName == "Car B" && it.odometer == 50000 }
        assertNull(carBLower.distance)
    }

    @Test
    fun `observe picks first matching previous entity with lower odometer`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        repository.create(BigDecimal("42.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100300, 2L)
        repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100800, 3L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        val higher = result.first { it.odometer == 100800 }
        assertEquals(500, higher.distance)

        val middle = result.first { it.odometer == 100300 }
        assertEquals(300, middle.distance)

        val lower = result.first { it.odometer == 100000 }
        assertNull(lower.distance)
    }

    @Test
    fun `observe with search query also calculates distance`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100500, 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "Car A").first()

        val higher = result.first { it.odometer == 100500 }
        assertEquals(500, higher.distance)
    }

    @Test
    fun `observe calculates distance independently per fuel type for same car`() = runTest {
        repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "LPG", "Car A", 100000, 1L)
        repository.create(BigDecimal("30.0"), BigDecimal("2.50"), "EUR", "Gasoline", "Car A", 100200, 3L)
        repository.create(BigDecimal("42.0"), BigDecimal("2.00"), "EUR", "LPG", "Car A", 100600, 2L)
        repository.create(BigDecimal("35.0"), BigDecimal("2.50"), "EUR", "Gasoline", "Car A", 100900, 4L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(4, result.size)

        val lpgHigher = result.first { it.type == "LPG" && it.odometer == 100600 }
        assertEquals(600, lpgHigher.distance)

        val lpgLower = result.first { it.type == "LPG" && it.odometer == 100000 }
        assertNull(lpgLower.distance)

        val gasolineHigher = result.first { it.type == "Gasoline" && it.odometer == 100900 }
        assertEquals(700, gasolineHigher.distance)

        val gasolineLower = result.first { it.type == "Gasoline" && it.odometer == 100200 }
        assertNull(gasolineLower.distance)
    }

    @Test
    fun `get always returns null distance`() = runTest {
        val id1 = repository.create(BigDecimal("40.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100000, 1L)
        val id2 = repository.create(BigDecimal("45.0"), BigDecimal("2.00"), "EUR", "Diesel", "Car A", 100500, 2L)

        val fc1 = repository.get(id1)
        assertNotNull(fc1)
        assertNull(fc1?.distance)

        val fc2 = repository.get(id2)
        assertNotNull(fc2)
        assertNull(fc2?.distance)
    }
}
