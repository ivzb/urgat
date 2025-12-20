package com.ivzb.urgat.domain.common

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ObserveUseCase<T, FilterType>(
    repository: BaseRepository<T>,
    filter: ((List<T>, FilterType) -> List<T>)? = null,
    transform: ((List<T>) -> List<T>)? = null,
): DefaultObserveUseCase<T, FilterType>(repository, filter, transform) {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<T>>> =
        invokeFlow(searchQueryFlow, filterTypeFlow, limitFlow)
            .map<List<T>, Async<List<T>>> { Async.Success(it) }
            .catch { e ->
                // todo: use error tracker instead

                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }

                emit(Async.Error)
            }

}