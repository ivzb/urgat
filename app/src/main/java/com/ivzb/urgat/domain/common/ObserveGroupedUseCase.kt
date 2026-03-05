package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ObserveGroupedUseCase<T, FilterType, SortType, Group>(
    repository: BaseRepository<T>,
    filter: ((List<T>, FilterType) -> List<T>)? = null,
    sort: ((List<T>, SortType) -> List<T>)? = null,
    transform: ((List<T>) -> List<T>)? = null,
    private val group: (List<T>) -> Group,
) : DefaultObserveUseCase<T, FilterType, SortType>(repository, filter, sort, transform) {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<FilterType>? = null,
        sortTypeFlow: Flow<SortType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<Async<Group>> =
        invokeFlow(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)
            .map { items -> group(items) }
            .map<Group, Async<Group>> { Async.Success(it) }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }

}