package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias GroupedFuelConsumption = GroupedItems<String, List<FuelConsumptionTotal>, FuelConsumption>

interface ObserveGroupedFuelConsumptionMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumption>>>

}

class DefaultObserveGroupedFuelConsumptionMonthlyUseCase @Inject constructor(
    repository: FuelConsumptionRepository
): ObserveGroupedFuelConsumptionMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase<FuelConsumption, Unit, FuelConsumptionSortType, List<GroupedFuelConsumption>>(
        repository = repository,
        sort = ::sortFuelConsumption,
        group = { items -> Group.byDateMonthly(items, FuelConsumption::date, FuelConsumption.Companion::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumption>>> = observeGroupedUseCase(
        searchQueryFlow = searchQueryFlow,
        dateRangeFlow = dateRangeFlow,
        filterTypeFlow = filterTypeFlow,
        sortTypeFlow = sortTypeFlow,
        limitFlow = limitFlow,
    )

}