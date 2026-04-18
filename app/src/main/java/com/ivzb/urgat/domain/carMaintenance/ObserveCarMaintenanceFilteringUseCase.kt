package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterMenuItem
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveCarMaintenanceFilteringUseCase {

    operator fun invoke(): Flow<Async<List<CarMaintenanceFilterMenuItem>>>

}

class DefaultObserveCarMaintenanceFilteringUseCase @Inject constructor(
    private val repository: CarMaintenanceRepository
) : ObserveCarMaintenanceFilteringUseCase {

    override fun invoke(): Flow<Async<List<CarMaintenanceFilterMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE)
            .map {
                if (it.isEmpty()) {
                    return@map listOf()
                }

                listOf(FilterMenuItem(type = CarMaintenanceFilterType_All, labelResId = R.string.filter_all, size = it.size)) +
                    it.groupBy { it.carName }
                        .entries
                        .sortedByDescending { it.value.size }
                        .map { FilterMenuItem(type = it.key, label = it.key, size = it.value.size) }
            }
            .map<List<CarMaintenanceFilterMenuItem>, Async<List<CarMaintenanceFilterMenuItem>>> { Async.Success(it) }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }
    }

}

