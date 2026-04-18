package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias GroupedCarMaintenance = GroupedItems<String, Int, CarMaintenance>

interface ObserveGroupedCarMaintenanceMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<String>? = null,
        sortTypeFlow: Flow<CarMaintenanceSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedCarMaintenance>>>

}

class DefaultObserveGroupedCarMaintenanceMonthlyUseCase @Inject constructor(
    repository: CarMaintenanceRepository
): ObserveGroupedCarMaintenanceMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterCarMaintenance,
        sort = ::sortCarMaintenance,
        group = { items -> Group.byDateMonthly(items, CarMaintenance::date) { it.size } },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<CarMaintenanceSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedCarMaintenance>>> = observeGroupedUseCase(
        searchQueryFlow = searchQueryFlow,
        dateRangeFlow = dateRangeFlow,
        filterTypeFlow = filterTypeFlow,
        sortTypeFlow = sortTypeFlow,
        limitFlow = limitFlow,
    )

}

