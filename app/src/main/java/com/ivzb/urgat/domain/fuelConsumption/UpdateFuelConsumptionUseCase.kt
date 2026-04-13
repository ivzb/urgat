package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateFuelConsumptionUseCase {

    suspend operator fun invoke(
        id: String,
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        type: String,
        carName: String,
        odometer: Int,
        date: Long,
    )

}

class DefaultUpdateFuelConsumptionUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): UpdateFuelConsumptionUseCase {

    override suspend operator fun invoke(
        id: String,
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        type: String,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        repository.update(id, amount, price, currency, type, carName, odometer, date)
    }

}

