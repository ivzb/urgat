package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import javax.inject.Inject

interface DeleteCarMaintenanceUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteCarMaintenanceUseCase @Inject constructor(
    private val repository: CarMaintenanceRepository
): DeleteCarMaintenanceUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}

