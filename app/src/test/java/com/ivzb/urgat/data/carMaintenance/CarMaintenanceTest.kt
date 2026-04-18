package com.ivzb.urgat.data.carMaintenance

import org.junit.Test
import kotlin.test.assertEquals

class CarMaintenanceTest {

    @Test
    fun `summary returns description and part number`() {
        val cm = carMaintenance(description = "Oil change", partNumber = "ABC123")
        assertEquals("Oil change", cm.summary.text)
    }

    @Test
    fun `summary returns only description when part number is empty`() {
        val cm = carMaintenance(description = "Oil change", partNumber = "")
        assertEquals("Oil change", cm.summary.text)
    }

    @Test
    fun `summary handles different descriptions`() {
        assertEquals(
            listOf(
                "Oil change",
                "Brake pads",
                "Air filter",
            ),
            listOf(
                carMaintenance(description = "Oil change", partNumber = "P1").summary.text,
                carMaintenance(description = "Brake pads", partNumber = "").summary.text,
                carMaintenance(description = "Air filter", partNumber = "AF-200").summary.text,
            )
        )
    }

    @Test
    fun `formatCsv returns correct format`() {
        val cm = carMaintenance(
            id = "id1",
            description = "Oil change",
            partNumber = "P1",
            url = "https://example.com",
            carName = "Car A",
            odometer = 150000,
            date = 1000L,
        )
        assertEquals(
            "\"id1\", \"Oil change\", \"P1\", \"https://example.com\", \"Car A\", 150000, 1000, 1000",
            cm.formatCsv()
        )
    }

    @Test
    fun `formatCsv with null url`() {
        val cm = carMaintenance(url = null)
        assert(cm.formatCsv().contains("\"null\""))
    }

    private fun carMaintenance(
        id: String = "cm",
        description: String = "Oil change",
        partNumber: String = "ABC123",
        url: String? = null,
        carName: String = "Mercedes w140",
        odometer: Int = 150000,
        date: Long = 1000L,
    ) = CarMaintenance(
        id = id,
        description = description,
        partNumber = partNumber,
        url = url,
        carName = carName,
        odometer = odometer,
        date = date,
        isDeleted = false,
        createdAt = date,
    )
}

