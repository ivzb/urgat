package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupFuelConsumptionCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedFuelConsumptionCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumptionCategories>>>

}

class DefaultObserveGroupedFuelConsumptionCategoriesMonthlyUseCase @Inject constructor(
    FuelConsumptionRepository: FuelConsumptionRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedFuelConsumptionCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = FuelConsumptionRepository,
        filter = ::filterFuelConsumption,
        sort = ::sortFuelConsumption,
        group = { FuelConsumption -> groupFuelConsumptionCategoriesMonthly(FuelConsumption, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumptionCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
