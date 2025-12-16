package com.ivzb.urgat.domain.default

import androidx.annotation.StringRes
import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ObserveGroupedUseCase<T, FilterType, Group>(
    repository: BaseRepository<T>,
    filter: ((List<T>, FilterType) -> List<T>)? = null,
    transform: ((List<T>) -> List<T>)? = null,
    private val group: (List<T>) -> List<GroupedItems<Group, T>>,
    @StringRes
    private val errorMessageResId: Int,
) : DefaultObserveUseCase<T, FilterType>(repository, filter, transform) {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedItems<Group, T>>>> =
        invokeFlow(searchQueryFlow, filterTypeFlow, limitFlow)
            .map { items -> group(items) }
            .map<List<GroupedItems<Group, T>>, Async<List<GroupedItems<Group, T>>>> { Async.Success(it) }
            .catch { e ->
                // todo: use error tracker instead

                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }

                emit(Async.Error(errorMessageResId, e))
            }

}