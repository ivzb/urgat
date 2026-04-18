package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import javax.inject.Inject

interface GetCarMaintenanceUseCase {

    suspend operator fun invoke(id: String): CarMaintenance?

}

class DefaultGetCarMaintenanceUseCase @Inject constructor(
    private val repository: CarMaintenanceRepository
): GetCarMaintenanceUseCase {

    override suspend fun invoke(id: String): CarMaintenance? {
        return repository.get(id)
    }

}

