package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import javax.inject.Inject

interface GetLastUsedFuelConsumptionCurrencyUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedFuelConsumptionCurrencyUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
): GetLastUsedFuelConsumptionCurrencyUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCurrency()
    }

}

