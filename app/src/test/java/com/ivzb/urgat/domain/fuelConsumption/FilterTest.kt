package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal.Companion.sumByType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class FilterTest {

    @Test
    fun `filterFuelConsumption returns all items for all filter`() {
        val items = listOf(
            fuelConsumption("1", "Diesel"),
            fuelConsumption("2", "Petrol"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilterType_All)

        assertEquals(items, result)
    }

    @Test
    fun `filterFuelConsumption returns only matching fuel type`() {
        val items = listOf(
            fuelConsumption("1", "Diesel"),
            fuelConsumption("2", "Petrol"),
            fuelConsumption("3", "Diesel"),
        )

        val result = filterFuelConsumption(items, "Diesel")

        assertEquals(listOf(items[0], items[2]), result)
    }

    @Test
    fun `sumByType aggregates totals by fuel type`() {
        val totals = listOf(
            FuelConsumptionTotal(type = "Diesel", amount = BigDecimal("45.5")),
            FuelConsumptionTotal(type = "Petrol", amount = BigDecimal("40.0")),
            FuelConsumptionTotal(type = "Diesel", amount = BigDecimal("42.0")),
        )

        val result = totals.sumByType().sortedBy { it.type }

        assertEquals(
            listOf(
                FuelConsumptionTotal(type = "Diesel", amount = BigDecimal("87.5")),
                FuelConsumptionTotal(type = "Petrol", amount = BigDecimal("40.0")),
            ),
            result,
        )
    }

    private fun fuelConsumption(
        id: String,
        type: String,
    ) = FuelConsumption(
        id = id,
        amount = BigDecimal("45.5"),
        price = BigDecimal("2.35"),
        currency = "EUR",
        type = type,
        carName = "Mercedes w140",
        odometer = 150000,
        date = 1000L,
        isDeleted = false,
        createdAt = 1000L,
    )
}

