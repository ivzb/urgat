package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveFuelConsumptionUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<FuelConsumptionSortType>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<FuelConsumption>>>

}

class DefaultObserveFuelConsumptionUseCase @Inject constructor(
    repository: FuelConsumptionRepository
): ObserveFuelConsumptionUseCase {

    private val observeUseCase = ObserveUseCase<FuelConsumption, Unit, FuelConsumptionSortType>(
        repository = repository,
        sort = ::sortFuelConsumption,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<FuelConsumption>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}

