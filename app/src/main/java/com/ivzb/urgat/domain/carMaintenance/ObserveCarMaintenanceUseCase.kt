package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

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
    repository: CarMaintenanceRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveCarMaintenanceUseCase {

    private val observeUseCase = ObserveUseCase<CarMaintenance, Unit, CarMaintenanceSortType>(
        repository = repository,
        dispatcher = dispatcher,
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
