package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import javax.inject.Inject

interface CreateCarMaintenanceUseCase {

    suspend operator fun invoke(
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    )

}

class DefaultCreateCarMaintenanceUseCase @Inject constructor(
    private val repository: CarMaintenanceRepository
): CreateCarMaintenanceUseCase {

    override suspend operator fun invoke(
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        repository.create(description, partNumber, url, carName, odometer, date)
    }

}

