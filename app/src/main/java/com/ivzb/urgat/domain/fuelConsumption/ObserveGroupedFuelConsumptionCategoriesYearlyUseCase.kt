package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionTotal
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupFuelConsumptionCategoriesYearly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilter
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias GroupedFuelConsumptionCategories = GroupedItems<String, List<FuelConsumptionTotal>, FuelConsumptionCategory>

interface ObserveGroupedFuelConsumptionCategoriesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<FuelConsumptionFilter>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumptionCategories>>>

}

class DefaultObserveGroupedFuelConsumptionCategoriesYearlyUseCase @Inject constructor(
    investmentsRepository: FuelConsumptionRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedFuelConsumptionCategoriesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = investmentsRepository,
        filter = ::filterFuelConsumption,
        sort = ::sortFuelConsumption,
        group = { investments -> groupFuelConsumptionCategoriesYearly(investments, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<FuelConsumptionFilter>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumptionCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
