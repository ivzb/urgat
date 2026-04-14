package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveFuelConsumptionFilteringUseCase {

    operator fun invoke(): Flow<Async<List<FuelConsumptionFilterMenuItem>>>

}

class DefaultObserveFuelConsumptionFilteringUseCase @Inject constructor(
    private val repository: FuelConsumptionRepository
) : ObserveFuelConsumptionFilteringUseCase {

    override fun invoke(): Flow<Async<List<FuelConsumptionFilterMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE)
            .map {
                if (it.isEmpty()) {
                    return@map listOf()
                }

                listOf(FilterMenuItem(type = FuelConsumptionFilterType_All, labelResId = R.string.filter_all, size = it.size)) +
                    it.groupBy { it.type }
                        .entries
                        .sortedByDescending { it.value.size }
                        .map { FilterMenuItem(type = it.key, label = it.key, size = it.value.size) }
            }
            .map<List<FuelConsumptionFilterMenuItem>, Async<List<FuelConsumptionFilterMenuItem>>> { Async.Success(it) }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }
    }

}
