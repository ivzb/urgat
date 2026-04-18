package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import org.junit.Assert.assertEquals
import org.junit.Test

class SortCarMaintenanceTest {

    private val items = listOf(
        CarMaintenance("cm_1", "Oil change", "P1", null, "Car A", 150000, 3000L, false, 1000L),
        CarMaintenance("cm_2", "Brake pads", "P2", null, "Car A", 140000, 1000L, false, 2000L),
        CarMaintenance("cm_3", "Air filter", "P3", null, "Car A", 160000, 2000L, false, 3000L),
    )

    @Test
    fun `BY_DATE sorts by date descending`() {
        val sorted = sortCarMaintenance(items, CarMaintenanceSortType.BY_DATE)

        assertEquals("cm_1", sorted[0].id) // date 3000
        assertEquals("cm_3", sorted[1].id) // date 2000
        assertEquals("cm_2", sorted[2].id) // date 1000
    }

    @Test
    fun `BY_ODOMETER_MIN sorts by odometer ascending`() {
        val sorted = sortCarMaintenance(items, CarMaintenanceSortType.BY_ODOMETER_MIN)

        assertEquals("cm_2", sorted[0].id) // odometer 140000
        assertEquals("cm_1", sorted[1].id) // odometer 150000
        assertEquals("cm_3", sorted[2].id) // odometer 160000
    }

    @Test
    fun `BY_ODOMETER_MAX sorts by odometer descending`() {
        val sorted = sortCarMaintenance(items, CarMaintenanceSortType.BY_ODOMETER_MAX)

        assertEquals("cm_3", sorted[0].id) // odometer 160000
        assertEquals("cm_1", sorted[1].id) // odometer 150000
        assertEquals("cm_2", sorted[2].id) // odometer 140000
    }

    @Test
    fun `sorting empty list returns empty list`() {
        val sorted = sortCarMaintenance(emptyList(), CarMaintenanceSortType.BY_DATE)
        assertEquals(emptyList<CarMaintenance>(), sorted)
    }

    @Test
    fun `sorting single item list returns same list`() {
        val single = listOf(items.first())
        val sorted = sortCarMaintenance(single, CarMaintenanceSortType.BY_DATE)
        assertEquals(single, sorted)
    }

    @Test
    fun `BY_ODOMETER_MIN with equal odometers preserves relative order`() {
        val sameOdometer = listOf(
            CarMaintenance("a", "Oil", "P1", null, "Car", 100000, 1L, false, 1L),
            CarMaintenance("b", "Filter", "P2", null, "Car", 100000, 2L, false, 2L),
        )
        val sorted = sortCarMaintenance(sameOdometer, CarMaintenanceSortType.BY_ODOMETER_MIN)
        assertEquals("a", sorted[0].id)
        assertEquals("b", sorted[1].id)
    }
}

