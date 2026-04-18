package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import javax.inject.Inject

interface GetLastUsedCarMaintenanceCarNameUseCase {

    suspend operator fun invoke(): String?

}

class DefaultGetLastUsedCarMaintenanceCarNameUseCase @Inject constructor(
    private val repository: CarMaintenanceRepository
): GetLastUsedCarMaintenanceCarNameUseCase {

    override suspend fun invoke(): String? {
        return repository.getLastUsedCarName()
    }

}

