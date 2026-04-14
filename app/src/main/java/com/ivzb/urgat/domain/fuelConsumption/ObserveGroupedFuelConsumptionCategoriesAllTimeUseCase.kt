package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupFuelConsumptionCategoriesAllTime
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedFuelConsumptionCategoriesAllTimeUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<FuelConsumptionCategory>>>

}

class DefaultObserveGroupedFuelConsumptionCategoriesAllTimeUseCase @Inject constructor(
    fuelConsumptionRepository: FuelConsumptionRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedFuelConsumptionCategoriesAllTimeUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = fuelConsumptionRepository,
        filter = ::filterFuelConsumption,
        sort = ::sortFuelConsumption,
        group = { FuelConsumption -> groupFuelConsumptionCategoriesAllTime(FuelConsumption, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<FuelConsumptionCategory>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
