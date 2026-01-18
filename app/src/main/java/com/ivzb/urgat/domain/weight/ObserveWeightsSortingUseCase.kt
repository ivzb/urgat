package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveWeightsSortingUseCase {

    operator fun invoke(): Flow<Async<List<WeightsSortMenuItem>>>

}

class DefaultObserveWeightsSortingUseCase @Inject constructor() : ObserveWeightsSortingUseCase {

    override fun invoke(): Flow<Async<List<WeightsSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = WeightsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = WeightsSortType.BY_MIN, labelResId = R.string.sort_by_min),
            SortMenuItem(type = WeightsSortType.BY_MAX, labelResId = R.string.sort_by_max),
        )))
    }

}
