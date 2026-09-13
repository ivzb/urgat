package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal.Companion.sumByType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class FilterTest {

    @Test
    fun `filterFuelConsumption returns all items when nothing is filtered`() {
        val items = listOf(
            fuelConsumption("1", type = "Diesel", carName = "Mercedes w140"),
            fuelConsumption("2", type = "Petrol", carName = "BMW e39"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilter.None)

        assertEquals(items, result)
    }

    @Test
    fun `filterFuelConsumption returns only matching fuel type regardless of car`() {
        val items = listOf(
            fuelConsumption("1", type = "Diesel", carName = "Mercedes w140"),
            fuelConsumption("2", type = "Petrol", carName = "BMW e39"),
            fuelConsumption("3", type = "Diesel", carName = "BMW e39"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilter(type = "Diesel"))

        assertEquals(listOf(items[0], items[2]), result)
    }

    @Test
    fun `filterFuelConsumption returns only matching car regardless of fuel type`() {
        val items = listOf(
            fuelConsumption("1", type = "Diesel", carName = "Mercedes w140"),
            fuelConsumption("2", type = "Petrol", carName = "BMW e39"),
            fuelConsumption("3", type = "Petrol", carName = "Mercedes w140"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilter(carName = "Mercedes w140"))

        assertEquals(listOf(items[0], items[2]), result)
    }

    @Test
    fun `filterFuelConsumption applies fuel type and car together`() {
        val items = listOf(
            fuelConsumption("1", type = "Diesel", carName = "Mercedes w140"),
            fuelConsumption("2", type = "Petrol", carName = "Mercedes w140"),
            fuelConsumption("3", type = "Diesel", carName = "BMW e39"),
            fuelConsumption("4", type = "Diesel", carName = "Mercedes w140"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilter(type = "Diesel", carName = "Mercedes w140"))

        assertEquals(listOf(items[0], items[3]), result)
    }

    @Test
    fun `filterFuelConsumption returns empty when fuel type and car never occur together`() {
        val items = listOf(
            fuelConsumption("1", type = "Diesel", carName = "Mercedes w140"),
            fuelConsumption("2", type = "Petrol", carName = "BMW e39"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilter(type = "Petrol", carName = "Mercedes w140"))

        assertEquals(emptyList<FuelConsumption>(), result)
    }

    @Test
    fun `filterFuelConsumption returns empty when nothing matches`() {
        val items = listOf(
            fuelConsumption("1", type = "Diesel", carName = "Mercedes w140"),
            fuelConsumption("2", type = "Petrol", carName = "BMW e39"),
        )

        val result = filterFuelConsumption(items, FuelConsumptionFilter(carName = "Audi a6"))

        assertEquals(emptyList<FuelConsumption>(), result)
    }

    @Test
    fun `filterFuelConsumption returns empty for empty input`() {
        val result = filterFuelConsumption(emptyList(), FuelConsumptionFilter(carName = "Mercedes w140"))

        assertEquals(emptyList<FuelConsumption>(), result)
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
        type: String = "Diesel",
        carName: String = "Mercedes w140",
    ) = FuelConsumption(
        id = id,
        amount = BigDecimal("45.5"),
        price = BigDecimal("2.35"),
        currency = "EUR",
        type = type,
        carName = carName,
        odometer = 150000,
        distance = 500,
        date = 1000L,
        isDeleted = false,
        createdAt = 1000L,
    )
}

