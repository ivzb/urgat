package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import javax.inject.Inject

interface GetLastUsedFuelConsumptionCarNameUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedFuelConsumptionCarNameUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): GetLastUsedFuelConsumptionCarNameUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCarName()
    }

}

