package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveWeightsSortingUseCase {

    operator fun invoke(): Flow<Async<List<WeightsSortMenuItem>>>

}

class DefaultObserveWeightsSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveWeightsSortingUseCase {

    override fun invoke(): Flow<Async<List<WeightsSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = WeightsSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = WeightsSortType.BY_MIN, labelResId = R.string.sort_by_min),
                SortMenuItem(type = WeightsSortType.BY_MAX, labelResId = R.string.sort_by_max),
            )
        )
    ).flowOn(dispatcher)

}
