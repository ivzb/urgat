package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveFuelConsumptionSortingUseCase {

    operator fun invoke(): Flow<Async<List<FuelConsumptionSortMenuItem>>>

}

class DefaultObserveFuelConsumptionSortingUseCase @Inject constructor() : ObserveFuelConsumptionSortingUseCase {

    override fun invoke(): Flow<Async<List<FuelConsumptionSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = FuelConsumptionSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = FuelConsumptionSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = FuelConsumptionSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )))
    }

}

