package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterTest {

    @Test
    fun `filterCarMaintenance returns all items for all filter`() {
        val items = listOf(
            carMaintenance("1", "Car A"),
            carMaintenance("2", "Car B"),
        )

        val result = filterCarMaintenance(items, CarMaintenanceFilterType_All)

        assertEquals(items, result)
    }

    @Test
    fun `filterCarMaintenance returns only matching car name`() {
        val items = listOf(
            carMaintenance("1", "Car A"),
            carMaintenance("2", "Car B"),
            carMaintenance("3", "Car A"),
        )

        val result = filterCarMaintenance(items, "Car A")

        assertEquals(listOf(items[0], items[2]), result)
    }

    @Test
    fun `filterCarMaintenance returns empty list when no match`() {
        val items = listOf(
            carMaintenance("1", "Car A"),
            carMaintenance("2", "Car B"),
        )

        val result = filterCarMaintenance(items, "Car C")

        assertEquals(emptyList<CarMaintenance>(), result)
    }

    @Test
    fun `filterCarMaintenance with empty list returns empty list`() {
        val result = filterCarMaintenance(emptyList(), CarMaintenanceFilterType_All)

        assertEquals(emptyList<CarMaintenance>(), result)
    }

    private fun carMaintenance(id: String, carName: String) = CarMaintenance(
        id = id,
        description = "Oil change",
        partNumber = "ABC123",
        url = null,
        carName = carName,
        odometer = 150000,
        date = 1000L,
        isDeleted = false,
        createdAt = 1000L,
    )
}

