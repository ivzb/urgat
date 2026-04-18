package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveCarMaintenanceUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<CarMaintenanceSortType>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<CarMaintenance>>>

}

class DefaultObserveCarMaintenanceUseCase @Inject constructor(
    repository: CarMaintenanceRepository
): ObserveCarMaintenanceUseCase {

    private val observeUseCase = ObserveUseCase<CarMaintenance, Unit, CarMaintenanceSortType>(
        repository = repository,
        sort = ::sortCarMaintenance,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<CarMaintenanceSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<CarMaintenance>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}

