package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import javax.inject.Inject

interface DeleteFuelConsumptionUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteFuelConsumptionUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): DeleteFuelConsumptionUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}

