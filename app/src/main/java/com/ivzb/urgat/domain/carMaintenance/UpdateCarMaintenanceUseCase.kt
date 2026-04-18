package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import javax.inject.Inject

interface UpdateCarMaintenanceUseCase {

    suspend operator fun invoke(
        id: String,
        description: String,
        partNumber: String,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    )

}

class DefaultUpdateCarMaintenanceUseCase @Inject constructor(
    private val repository: CarMaintenanceRepository
): UpdateCarMaintenanceUseCase {

    override suspend operator fun invoke(
        id: String,
        description: String,
        partNumber: String,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        repository.update(id, description, partNumber, url, carName, odometer, date)
    }

}

