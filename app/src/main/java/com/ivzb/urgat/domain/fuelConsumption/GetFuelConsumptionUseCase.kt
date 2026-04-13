package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import javax.inject.Inject

interface GetFuelConsumptionUseCase {

    suspend operator fun invoke(id: String): FuelConsumption?

}

class DefaultGetFuelConsumptionUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): GetFuelConsumptionUseCase {

    override suspend fun invoke(id: String): FuelConsumption? {
        return repository.get(id)
    }

}

