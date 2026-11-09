package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.weights.WeightsFilteringUiInfo
import com.ivzb.urgat.ui.weights.WeightsSortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.ui.weights.WeightsUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface ObserveWeightsUiStateUseCase {

    operator fun invoke(
        sortTypeFlow: Flow<WeightsSortType>,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<WeightsUiState>>

}

class DefaultObserveWeightsUiStateUseCase @Inject constructor(
    private val observeWeightsUseCase: ObserveWeightsUseCase,
    private val observeWeightsSortingUseCase: ObserveWeightsSortingUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveWeightsUiStateUseCase {

    override fun invoke(
        sortTypeFlow: Flow<WeightsSortType>,
        limitFlow: Flow<Int>?,
    ): Flow<Async<WeightsUiState>> = combineUiState(
        observeWeightsUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = sortTypeFlow, limitFlow = limitFlow),
        observeWeightsSortingUseCase(),
        sortTypeFlow.map { Async.Success(it) },

        onLoading = {
            WeightsUiState(isLoading = true)
        },
        onError = {
            WeightsUiState(hasError = true)
        },
        onSuccess = { weights, sorting, sortType ->
            WeightsUiState(
                weights = weights,
                isLoading = false,
                filteringUiInfo = getFilterUiInfo(
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }
        .flowOn(dispatcher)

    private fun getFilterUiInfo(
        sortType: WeightsSortType,
        sortMenuItems: List<WeightsSortMenuItem>,
    ): WeightsFilteringUiInfo {
        val showSortBadge = sortType != WeightsSortType.BY_DATE

        return WeightsFilteringUiInfo(
            currentFilteringLabel = R.string.all_weights,
            sortMenuItems = sortMenuItems,
            showSortBadge = showSortBadge,
        )
    }

}
