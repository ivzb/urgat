package com.ivzb.urgat.data.fuelConsumption

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class FuelConsumptionTest {

    @Test
    fun `summary returns expected strings for multiple fuel consumption cases`() {
        assertEquals(
            listOf(
                "45.5L Diesel | 2.35 EUR | 9.1L/100km",
                "45.5L Diesel | 2.35 EUR",
                "45.5L Diesel | 2.35 EUR",
                "45.5L Diesel | 2.35 EUR",
                "40L Diesel | 2.4 USD | 5.0L/100km",
                "12.5L Premium | 3.99 GBP | 6.2L/100km",
                "50.99L LPG | 1.99 BGN | 15.3L/100km",
                "1.23L Gasoline | 0.99 CHF | 1.2L/100km",
            ),
            listOf(
                fuelConsumption(currency = "eur", distance = 500, date = 1L).summary.text,
                fuelConsumption(distance = null, date = 2L).summary.text,
                fuelConsumption(distance = 0, date = 3L).summary.text,
                fuelConsumption(distance = -1, date = 4L).summary.text,
                fuelConsumption(
                    amount = "40.0",
                    price = "2.4",
                    currency = "usd",
                    distance = 800,
                    date = 5L,
                ).summary.text,
                fuelConsumption(
                    amount = "12.5",
                    price = "3.99",
                    currency = "gbp",
                    type = "Premium",
                    distance = 200,
                    date = 6L,
                ).summary.text,
                fuelConsumption(
                    amount = "50.99",
                    price = "1.999",
                    currency = "bgn",
                    type = "LPG",
                    distance = 333,
                    date = 7L,
                ).summary.text,
                fuelConsumption(
                    amount = "1.23",
                    price = "0.99",
                    currency = "chf",
                    type = "Gasoline",
                    distance = 100,
                    date = 8L,
                ).summary.text,
            ),
        )
    }

    private fun fuelConsumption(
        id: String = "fc",
        amount: String = "45.5",
        price: String = "2.35",
        currency: String = "EUR",
        type: String = "Diesel",
        carName: String = "Mercedes w140",
        odometer: Int = 150000,
        distance: Int?,
        date: Long,
    ) = FuelConsumption(
        id = id,
        amount = BigDecimal(amount),
        price = BigDecimal(price),
        currency = currency,
        type = type,
        carName = carName,
        odometer = odometer,
        distance = distance,
        date = date,
        isDeleted = false,
        createdAt = date,
    )
}






