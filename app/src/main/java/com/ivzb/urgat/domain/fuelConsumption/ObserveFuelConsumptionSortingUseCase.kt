package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveFuelConsumptionSortingUseCase {

    operator fun invoke(): Flow<Async<List<FuelConsumptionSortMenuItem>>>

}

class DefaultObserveFuelConsumptionSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveFuelConsumptionSortingUseCase {

    override fun invoke(): Flow<Async<List<FuelConsumptionSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = FuelConsumptionSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = FuelConsumptionSortType.BY_MIN, labelResId = R.string.sort_by_min),
                SortMenuItem(type = FuelConsumptionSortType.BY_MAX, labelResId = R.string.sort_by_max),
            )
        )
    ).flowOn(dispatcher)

}
