package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import javax.inject.Inject

interface GetLastUsedFuelConsumptionTypeUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedFuelConsumptionTypeUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): GetLastUsedFuelConsumptionTypeUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedType()
    }

}

