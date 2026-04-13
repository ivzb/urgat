package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class SortFuelConsumptionTest {

    private val fuelConsumption = listOf(
        FuelConsumption("fc_1", BigDecimal("45.5"), BigDecimal("2.35"), "EUR", "Diesel", "Car A", 150000, 3000L, false, 1000L),
        FuelConsumption("fc_2", BigDecimal("40.0"), BigDecimal("2.40"), "EUR", "Diesel", "Car A", 149500, 1000L, false, 2000L),
        FuelConsumption("fc_3", BigDecimal("50.0"), BigDecimal("2.30"), "EUR", "Diesel", "Car A", 149000, 2000L, false, 3000L),
    )

    @Test
    fun `BY_DATE sorts by date descending`() {
        val sorted = sortFuelConsumption(fuelConsumption, FuelConsumptionSortType.BY_DATE)

        assertEquals("fc_1", sorted[0].id) // date 3000
        assertEquals("fc_3", sorted[1].id) // date 2000
        assertEquals("fc_2", sorted[2].id) // date 1000
    }

    @Test
    fun `BY_MIN sorts by price ascending`() {
        val sorted = sortFuelConsumption(fuelConsumption, FuelConsumptionSortType.BY_MIN)

        assertEquals("fc_3", sorted[0].id) // price 2.30
        assertEquals("fc_1", sorted[1].id) // price 2.35
        assertEquals("fc_2", sorted[2].id) // price 2.40
    }

    @Test
    fun `BY_MAX sorts by price descending`() {
        val sorted = sortFuelConsumption(fuelConsumption, FuelConsumptionSortType.BY_MAX)

        assertEquals("fc_2", sorted[0].id) // price 2.40
        assertEquals("fc_1", sorted[1].id) // price 2.35
        assertEquals("fc_3", sorted[2].id) // price 2.30
    }

    @Test
    fun `sorting empty list returns empty list`() {
        val sorted = sortFuelConsumption(emptyList(), FuelConsumptionSortType.BY_DATE)
        assertEquals(emptyList<FuelConsumption>(), sorted)
    }

    @Test
    fun `sorting single item list returns same list`() {
        val single = listOf(fuelConsumption.first())
        val sorted = sortFuelConsumption(single, FuelConsumptionSortType.BY_DATE)
        assertEquals(single, sorted)
    }

    @Test
    fun `BY_MIN with equal prices preserves relative order`() {
        val samePrice = listOf(
            FuelConsumption("a", BigDecimal("10"), BigDecimal("2.00"), "EUR", "Diesel", "Car", 100, 1L, false, 1L),
            FuelConsumption("b", BigDecimal("20"), BigDecimal("2.00"), "EUR", "Diesel", "Car", 200, 2L, false, 2L),
        )
        val sorted = sortFuelConsumption(samePrice, FuelConsumptionSortType.BY_MIN)
        assertEquals("a", sorted[0].id)
        assertEquals("b", sorted[1].id)
    }

}

