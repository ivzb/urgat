package com.ivzb.urgat.data.carMaintenance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CarMaintenanceMappingTest {

    @Test
    fun `toEntity maps all fields correctly`() {
        val carMaintenance = CarMaintenance(
            id = "cm1",
            description = "Oil change",
            partNumber = "ABC123",
            url = "https://example.com",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1000L,
            isDeleted = false,
            createdAt = 2000L,
        )

        val entity = carMaintenance.toEntity()

        assertEquals("cm1", entity.id)
        assertEquals("Oil change", entity.description)
        assertEquals("ABC123", entity.partNumber)
        assertEquals("https://example.com", entity.url)
        assertEquals("Mercedes w140", entity.carName)
        assertEquals(150000, entity.odometer)
        assertEquals(1000L, entity.date)
        assertEquals(false, entity.isDeleted)
        assertEquals(2000L, entity.createdAt)
    }

    @Test
    fun `toEntity maps null url`() {
        val carMaintenance = CarMaintenance(
            id = "cm1", description = "Oil", partNumber = "P1", url = null,
            carName = "Car", odometer = 100, date = 1L, isDeleted = false, createdAt = 1L,
        )

        assertNull(carMaintenance.toEntity().url)
    }

    @Test
    fun `toExternal maps all fields correctly`() {
        val entity = CarMaintenanceEntity(
            id = "cm1",
            description = "Oil change",
            partNumber = "ABC123",
            url = "https://example.com",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1000L,
            isDeleted = false,
            createdAt = 2000L,
        )

        val carMaintenance = entity.toExternal()

        assertEquals("cm1", carMaintenance.id)
        assertEquals("Oil change", carMaintenance.description)
        assertEquals("ABC123", carMaintenance.partNumber)
        assertEquals("https://example.com", carMaintenance.url)
        assertEquals("Mercedes w140", carMaintenance.carName)
        assertEquals(150000, carMaintenance.odometer)
        assertEquals(1000L, carMaintenance.date)
        assertEquals(false, carMaintenance.isDeleted)
        assertEquals(2000L, carMaintenance.createdAt)
    }

    @Test
    fun `toExternal maps null url`() {
        val entity = CarMaintenanceEntity(
            id = "cm1", description = "Oil", partNumber = "P1", url = null,
            carName = "Car", odometer = 100, date = 1L, isDeleted = false, createdAt = 1L,
        )

        assertNull(entity.toExternal().url)
    }

    @Test
    fun `roundtrip preserves all data`() {
        val original = CarMaintenance(
            id = "cm1",
            description = "Oil change",
            partNumber = "ABC123",
            url = "https://example.com",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 1000L,
            isDeleted = false,
            createdAt = 2000L,
        )

        val roundTripped = original.toEntity().toExternal()

        assertEquals(original, roundTripped)
    }
}

