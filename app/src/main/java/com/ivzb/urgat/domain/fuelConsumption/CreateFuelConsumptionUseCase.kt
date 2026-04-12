package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateFuelConsumptionUseCase {

    suspend operator fun invoke(
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        carName: String,
        odometer: Int,
        date: Long,
    )

}

class DefaultCreateFuelConsumptionUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): CreateFuelConsumptionUseCase {

    override suspend operator fun invoke(
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        repository.create(amount, price, currency, carName, odometer, date)
    }

}

