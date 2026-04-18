package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortMenuItem
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveCarMaintenanceSortingUseCase {

    operator fun invoke(): Flow<Async<List<CarMaintenanceSortMenuItem>>>

}

class DefaultObserveCarMaintenanceSortingUseCase @Inject constructor() : ObserveCarMaintenanceSortingUseCase {

    override fun invoke(): Flow<Async<List<CarMaintenanceSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = CarMaintenanceSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = CarMaintenanceSortType.BY_ODOMETER_MIN, labelResId = R.string.sort_by_odometer_min),
            SortMenuItem(type = CarMaintenanceSortType.BY_ODOMETER_MAX, labelResId = R.string.sort_by_odometer_max),
        )))
    }

}

